package kcs.funding.fundingboost.elasticsearch.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_ITEM;
import static kcs.funding.fundingboost.domain.entity.QBookmark.bookmark;
import static kcs.funding.fundingboost.domain.entity.QFundingItem.fundingItem;
import static kcs.funding.fundingboost.domain.entity.QItem.item;
import static kcs.funding.fundingboost.domain.entity.QOrderItem.orderItem;
import static kcs.funding.fundingboost.domain.entity.member.QMember.member;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.core.annotation.Counted;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;
import kcs.funding.fundingboost.domain.dto.response.home.HomeRankingItemDto;
import kcs.funding.fundingboost.domain.dto.response.shopping.ShopDto;
import kcs.funding.fundingboost.domain.dto.response.shoppingDetail.ItemDetailDto;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.member.MemberGender;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.bookmark.BookmarkRepository;
import kcs.funding.fundingboost.domain.repository.item.ItemRepository;
import kcs.funding.fundingboost.elasticsearch.index.ItemIndex;
import kcs.funding.fundingboost.elasticsearch.repository.ItemIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemIndexService {
    private static final Duration INDEX_SYNC_RETRY_INTERVAL = Duration.ofMinutes(5);

    private final ItemIndexRepository itemIndexRepository;
    private final ItemRepository itemRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final JPAQueryFactory queryFactory;
    private volatile long lastIndexedItemCount = -1L;
    private volatile LocalDateTime lastIndexedModifiedDate;
    private volatile LocalDateTime lastSyncAttemptAt;

    public Slice<ShopDto> searchWithCategoryAndName(String keyword, Pageable pageable) {
        ensureIndexReady();
        Slice<ItemIndex> items = itemIndexRepository.findByCategoryOrItemName(keyword, pageable);
        return items.map(ShopDto::fromIndex);
    }

    @Counted("ItemIndexService.getItemsUsingElasticsearch")
    public Slice<ShopDto> searchWithCategory(String keyword, Pageable pageable) {
        ensureIndexReady();
        Slice<ItemIndex> items;

        if (keyword == null || keyword.isBlank()) {
            Pageable sortedPageable = pageable.getSort().isSorted()
                    ? pageable
                    : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Order.desc("itemId")));
            Page<ItemIndex> page = itemIndexRepository.findAll(sortedPageable);
            items = page;
        } else {
            items = itemIndexRepository.findByCategory(keyword, pageable);
        }

        return items.map(ShopDto::fromIndex);
    }

    public List<String> getCategories() {
        ensureIndexReady();
        return StreamSupport.stream(itemIndexRepository.findAll(Sort.by(Sort.Direction.ASC, "category")).spliterator(), false)
                .map(ItemIndex::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .toList();
    }

    public ItemDetailDto getItemDetail(Long memberId, Long itemId) {
        ensureIndexReady();

        ItemIndex itemIndex = itemIndexRepository.findById(itemId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_ITEM));

        boolean bookmarked = memberId != null
                && bookmarkRepository.findBookmarkByMemberAndItem(memberId, itemId).isPresent();

        return ItemDetailDto.fromIndex(itemIndex, bookmarked);
    }

    public HomeRankingResult getHomeRankings(String rankingType, String audience, String priceRange, int size) {
        ensureIndexReady();
        String normalizedRankingType = normalizeRankingType(rankingType);
        String normalizedAudience = normalizeAudience(audience);
        String normalizedPriceRange = normalizePriceRange(priceRange);
        boolean fallbackApplied = false;

        List<ScoredItem> scoredItems = switch (normalizedRankingType) {
            case "purchase" -> getPurchaseRanking(normalizedAudience, normalizedPriceRange, size);
            case "wish" -> getWishRanking(normalizedAudience, normalizedPriceRange, size);
            default -> getFundingRanking(normalizedAudience, normalizedPriceRange, size);
        };

        if (scoredItems.isEmpty() && shouldUseFallback(normalizedRankingType, normalizedAudience, normalizedPriceRange)) {
            scoredItems = getFallbackRanking(normalizedAudience, normalizedPriceRange, size);
            fallbackApplied = !scoredItems.isEmpty();
        }

        return new HomeRankingResult(toHomeRankingItems(scoredItems), fallbackApplied);
    }

    synchronized void ensureIndexReady() {
        try {
            long databaseItemCount = itemRepository.count();
            IndexOperations indexOperations = elasticsearchOperations.indexOps(ItemIndex.class);

            if (!indexOperations.exists()) {
                indexOperations.createWithMapping();
            }

            LocalDateTime latestModifiedDate = itemRepository.findFirstByOrderByModifiedDateDesc()
                    .map(Item::getModifiedDate)
                    .orElse(null);

            boolean alreadySynced = databaseItemCount == lastIndexedItemCount
                    && Objects.equals(latestModifiedDate, lastIndexedModifiedDate);

            if (alreadySynced) {
                return;
            }

            if (databaseItemCount == 0) {
                // 빈 상태는 읽기 경로에서 굳이 전체 삭제를 강제하지 않는다.
                lastIndexedItemCount = 0L;
                lastIndexedModifiedDate = null;
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            if (lastSyncAttemptAt != null && now.isBefore(lastSyncAttemptAt.plus(INDEX_SYNC_RETRY_INTERVAL))) {
                return;
            }
            lastSyncAttemptAt = now;

            List<ItemIndex> itemIndexes = itemRepository.findAll(Sort.by(Sort.Direction.ASC, "itemId")).stream()
                    .map(ItemIndex::fromEntity)
                    .toList();

            // document id(itemId) 기준 upsert. read API 경로에서 deleteAll()로 블로킹하지 않는다.
            itemIndexRepository.saveAll(itemIndexes);
            lastIndexedItemCount = databaseItemCount;
            lastIndexedModifiedDate = latestModifiedDate;
            log.info("elasticsearch item index synced: {} docs", itemIndexes.size());
        } catch (Exception e) {
            // 인덱스 동기화 실패가 즉시 사용자 조회 실패(500)로 이어지지 않게 격리한다.
            String reason = e.getMessage();
            if (reason == null || reason.isBlank()) {
                reason = e.getClass().getSimpleName();
            } else if (reason.length() > 300) {
                reason = reason.substring(0, 300) + "...";
            }
            log.warn("elasticsearch item index sync skipped due to error: {}", reason);
        }
    }

    private List<HomeRankingItemDto> toHomeRankingItems(List<ScoredItem> scoredItems) {
        List<Long> itemIds = scoredItems.stream()
                .map(ScoredItem::itemId)
                .toList();

        Map<Long, ItemIndex> itemIndexMap = new HashMap<>();
        itemIndexRepository.findAllById(itemIds).forEach(index -> itemIndexMap.put(index.getItemId(), index));

        List<HomeRankingItemDto> results = new ArrayList<>();

        for (int i = 0; i < scoredItems.size(); i++) {
            ScoredItem scoredItem = scoredItems.get(i);
            ItemIndex itemIndex = itemIndexMap.get(scoredItem.itemId());
            if (itemIndex == null) {
                continue;
            }
            results.add(HomeRankingItemDto.fromIndex(itemIndex, scoredItem.score(), i + 1));
        }

        return results;
    }

    private List<ScoredItem> getFundingRanking(String audience, String priceRange, int size) {
        List<Tuple> rows = queryFactory
                .select(fundingItem.item.itemId, fundingItem.count())
                .from(fundingItem)
                .join(fundingItem.item, item)
                .join(fundingItem.funding.member, member)
                .where(memberGenderFilter(audience), priceRangeFilter(priceRange))
                .groupBy(fundingItem.item.itemId)
                .orderBy(fundingItem.count().desc(), fundingItem.item.itemId.desc())
                .limit(size)
                .fetch();

        return rows.stream()
                .map(row -> new ScoredItem(
                        row.get(fundingItem.item.itemId),
                        row.get(fundingItem.count()) == null ? 0L : row.get(fundingItem.count())))
                .toList();
    }

    private List<ScoredItem> getPurchaseRanking(String audience, String priceRange, int size) {
        List<Tuple> rows = queryFactory
                .select(orderItem.item.itemId, orderItem.quantity.sum())
                .from(orderItem)
                .join(orderItem.item, item)
                .join(orderItem.order.member, member)
                .where(memberGenderFilter(audience), priceRangeFilter(priceRange))
                .groupBy(orderItem.item.itemId)
                .orderBy(orderItem.quantity.sum().desc(), orderItem.item.itemId.desc())
                .limit(size)
                .fetch();

        return rows.stream()
                .map(row -> {
                    Integer score = row.get(orderItem.quantity.sum());
                    return new ScoredItem(row.get(orderItem.item.itemId), score == null ? 0L : score.longValue());
                })
                .toList();
    }

    private List<ScoredItem> getWishRanking(String audience, String priceRange, int size) {
        List<Tuple> rows = queryFactory
                .select(bookmark.item.itemId, bookmark.count())
                .from(bookmark)
                .join(bookmark.item, item)
                .join(bookmark.member, member)
                .where(memberGenderFilter(audience), priceRangeFilter(priceRange))
                .groupBy(bookmark.item.itemId)
                .orderBy(bookmark.count().desc(), bookmark.item.itemId.desc())
                .limit(size)
                .fetch();

        return rows.stream()
                .map(row -> new ScoredItem(
                        row.get(bookmark.item.itemId),
                        row.get(bookmark.count()) == null ? 0L : row.get(bookmark.count())))
                .toList();
    }

    private List<ScoredItem> getFallbackRanking(String audience, String priceRange, int size) {
        List<Item> items = queryFactory
                .selectFrom(item)
                .where(priceRangeFilter(priceRange))
                .orderBy(item.itemId.desc())
                .limit(size)
                .fetch();

        List<ScoredItem> fallback = new ArrayList<>();
        for (Item fallbackItem : items) {
            fallback.add(new ScoredItem(fallbackItem.getItemId(), 0L));
        }
        return fallback;
    }

    private boolean shouldUseFallback(String rankingType, String audience, String priceRange) {
        if (!"all".equals(audience) || !"all".equals(priceRange)) {
            return false;
        }

        return !hasAnyRankingData(rankingType);
    }

    private boolean hasAnyRankingData(String rankingType) {
        return switch (rankingType) {
            case "purchase" -> queryFactory
                    .selectOne()
                    .from(orderItem)
                    .fetchFirst() != null;
            case "wish" -> queryFactory
                    .selectOne()
                    .from(bookmark)
                    .fetchFirst() != null;
            default -> queryFactory
                    .selectOne()
                    .from(fundingItem)
                    .fetchFirst() != null;
        };
    }

    private BooleanExpression memberGenderFilter(String audience) {
        if ("all".equals(audience)) {
            return null;
        }

        return member.gender.eq("woman".equals(audience) ? MemberGender.WOMAN : MemberGender.MAN);
    }

    private BooleanExpression priceRangeFilter(String priceRange) {
        return switch (priceRange) {
            case "under10k" -> item.itemPrice.lt(10_000);
            case "10kto30k" -> item.itemPrice.goe(10_000).and(item.itemPrice.lt(30_000));
            case "30kto50k" -> item.itemPrice.goe(30_000).and(item.itemPrice.lt(50_000));
            case "over50k" -> item.itemPrice.goe(50_000);
            default -> null;
        };
    }

    private String normalizeRankingType(String rankingType) {
        if (rankingType == null || rankingType.isBlank()) {
            return "funding";
        }
        return rankingType.toLowerCase();
    }

    private String normalizeAudience(String audience) {
        if (audience == null || audience.isBlank()) {
            return "all";
        }
        return audience.toLowerCase();
    }

    private String normalizePriceRange(String priceRange) {
        if (priceRange == null || priceRange.isBlank()) {
            return "all";
        }
        return priceRange.toLowerCase();
    }

    private record ScoredItem(Long itemId, long score) {
    }

    public record HomeRankingResult(List<HomeRankingItemDto> items, boolean fallbackApplied) {
    }
}
