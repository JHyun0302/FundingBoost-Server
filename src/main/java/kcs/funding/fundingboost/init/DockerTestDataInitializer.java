package kcs.funding.fundingboost.init;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.entity.Bookmark;
import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.entity.OrderItem;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.entity.member.MemberGender;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.OrderRepository;
import kcs.funding.fundingboost.domain.repository.bookmark.BookmarkRepository;
import kcs.funding.fundingboost.domain.repository.contributor.ContributorRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.repository.fundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.item.ItemRepository;
import kcs.funding.fundingboost.domain.repository.orderItem.OrderItemRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.testdata", name = "enabled", havingValue = "true")
@Slf4j
public class DockerTestDataInitializer {

    private static final String QA_PASSWORD = "Test1234!";
    private static final long RANDOM_SEED = 20260228L;
    private static final List<SeedMemberDefinition> SEED_MEMBERS = List.of(
            new SeedMemberDefinition("마리오", "qa1@fundingboost.test", MemberGender.MAN, "seed_qa1", "/test-members/mario.png"),
            new SeedMemberDefinition("루이지", "qa2@fundingboost.test", MemberGender.MAN, "seed_qa2", "/test-members/luigi.png"),
            new SeedMemberDefinition("피치공주", "qa3@fundingboost.test", MemberGender.WOMAN, "seed_qa3", "/test-members/princess-peach.png"),
            new SeedMemberDefinition("키노피오", "qa4@fundingboost.test", MemberGender.MAN, "seed_qa4", "/test-members/toad.png"),
            new SeedMemberDefinition("요시", "qa5@fundingboost.test", MemberGender.WOMAN, "seed_qa5", "/test-members/yoshi.png")
    );
    private static final List<Tag> FUNDING_TAGS = List.of(
            Tag.BIRTHDAY, Tag.GRADUATE, Tag.ETC, Tag.BIRTHDAY, Tag.ETC
    );

    private final MemberRepository memberRepository;
    private final RelationshipRepository relationshipRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FundingRepository fundingRepository;
    private final FundingItemRepository fundingItemRepository;
    private final ContributorRepository contributorRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;

    private volatile boolean initialized = false;

    @Scheduled(
            initialDelayString = "${app.testdata.retry-delay-ms:10000}",
            fixedDelayString = "${app.testdata.retry-delay-ms:10000}"
    )
    @Transactional
    public synchronized void bootstrap() {
        if (initialized) {
            return;
        }

        if (memberRepository.findByEmail(SEED_MEMBERS.get(0).email()).isPresent()) {
            refreshSeedMemberProfiles();
            initialized = true;
            log.info("테스트 데이터가 이미 존재합니다. 추가 생성은 건너뜁니다.");
            return;
        }

        List<Item> items;
        try {
            items = itemRepository.findAll(Sort.by(Sort.Order.asc("category"), Sort.Order.asc("itemId")));
        } catch (RuntimeException ex) {
            log.info("item 스키마 준비 전입니다. 테스트 데이터 시드를 대기합니다.");
            return;
        }

        List<List<Item>> bookmarkPools = buildBookmarkPools(items);
        if (bookmarkPools == null) {
            return;
        }

        List<Member> members = seedMembers();
        createRelationships(members);

        Map<Member, List<Item>> memberBookmarks = createBookmarks(members, bookmarkPools);
        createDeliveriesAndOrders(members, memberBookmarks);
        List<Funding> fundings = createFundings(members, memberBookmarks);
        createContributors(members, fundings);

        initialized = true;
        log.info("Docker 테스트 데이터 생성 완료: members={}, bookmarks={}, fundings={}",
                members.size(),
                memberBookmarks.values().stream().mapToInt(List::size).sum(),
                fundings.size());
    }

    private List<Member> seedMembers() {
        String encodedPassword = passwordEncoder.encode(QA_PASSWORD);
        List<Member> members = new ArrayList<>();

        for (SeedMemberDefinition seedMember : SEED_MEMBERS) {
            members.add(Member.createMemberWithPoint(
                    seedMember.name(),
                    seedMember.email(),
                    encodedPassword,
                    seedMember.profileImgUrl(),
                    50_000,
                    seedMember.kakaoId(),
                    seedMember.gender()
            ));
        }

        return memberRepository.saveAll(members);
    }

    private void refreshSeedMemberProfiles() {
        for (SeedMemberDefinition seedMember : SEED_MEMBERS) {
            memberRepository.findByEmail(seedMember.email()).ifPresent(existingMember -> {
                if (!seedMember.profileImgUrl().equals(existingMember.getProfileImgUrl())) {
                    existingMember.changeProfileImgUrl(seedMember.profileImgUrl());
                }
                if (existingMember.getGender() != seedMember.gender()) {
                    existingMember.changeGender(seedMember.gender());
                }
            });
        }
    }

    private void createRelationships(List<Member> members) {
        List<Relationship> relationships = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            for (int j = i + 1; j < members.size(); j++) {
                relationships.addAll(Relationship.createRelationships(members.get(i), members.get(j)));
            }
        }

        relationshipRepository.saveAll(relationships);
    }

    private Map<Member, List<Item>> createBookmarks(List<Member> members, List<List<Item>> bookmarkPools) {
        Map<Member, List<Item>> memberBookmarks = new LinkedHashMap<>();
        List<Bookmark> bookmarks = new ArrayList<>();

        for (int memberIndex = 0; memberIndex < members.size(); memberIndex++) {
            Member member = members.get(memberIndex);
            List<Item> selectedItems = new ArrayList<>();

            for (int categoryIndex = 0; categoryIndex < bookmarkPools.size(); categoryIndex++) {
                List<Item> itemsInCategory = new ArrayList<>(bookmarkPools.get(categoryIndex));
                Collections.shuffle(itemsInCategory, new Random(RANDOM_SEED + (memberIndex * 31L) + (categoryIndex * 17L)));

                selectedItems.add(itemsInCategory.get(0));
                selectedItems.add(itemsInCategory.get(1));
            }

            memberBookmarks.put(member, selectedItems);
            for (Item selectedItem : selectedItems) {
                bookmarks.add(Bookmark.createBookmark(member, selectedItem));
            }
        }

        bookmarkRepository.saveAll(bookmarks);
        return memberBookmarks;
    }

    private void createDeliveriesAndOrders(List<Member> members, Map<Member, List<Item>> memberBookmarks) {
        List<Delivery> deliveries = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            deliveries.add(Delivery.createDelivery(
                    "서울특별시 송파구 올림픽로 " + (300 + i),
                    String.format("010-9000-10%02d", i + 1),
                    member.getNickName(),
                    member
            ));
        }

        deliveries = deliveryRepository.saveAll(deliveries);

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            Delivery delivery = deliveries.get(i);
            List<Item> bookmarkedItems = memberBookmarks.get(member);
            Item orderTarget = bookmarkedItems.get((i + 2) % bookmarkedItems.size());

            Order order = orderRepository.save(Order.createOrder(member, delivery));
            orderItemRepository.save(OrderItem.createOrderItem(order, orderTarget, (i % 2) + 1));
        }
    }

    private List<Funding> createFundings(List<Member> members, Map<Member, List<Item>> memberBookmarks) {
        List<Funding> fundings = new ArrayList<>();
        Set<Long> usedFundingItemIds = new HashSet<>();

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            List<Item> bookmarkedItems = memberBookmarks.get(member);
            Item fundingTarget = pickUniqueFundingTarget(bookmarkedItems, (i + 5) % bookmarkedItems.size(), usedFundingItemIds);
            usedFundingItemIds.add(fundingTarget.getItemId());

            Funding funding = fundingRepository.save(Funding.createFunding(
                    member,
                    member.getNickName() + "님의 테스트 펀딩",
                    FUNDING_TAGS.get(i % FUNDING_TAGS.size()),
                    LocalDateTime.now().plusDays(14 + i)
            ));
            fundingItemRepository.save(FundingItem.createFundingItem(funding, fundingTarget, 1));
            fundings.add(funding);
        }

        return fundings;
    }

    private Item pickUniqueFundingTarget(List<Item> bookmarkedItems, int preferredIndex, Set<Long> usedFundingItemIds) {
        for (int offset = 0; offset < bookmarkedItems.size(); offset++) {
            Item candidate = bookmarkedItems.get((preferredIndex + offset) % bookmarkedItems.size());
            if (!usedFundingItemIds.contains(candidate.getItemId())) {
                return candidate;
            }
        }

        return bookmarkedItems.get(preferredIndex);
    }

    private void createContributors(List<Member> members, List<Funding> fundings) {
        List<Contributor> contributors = new ArrayList<>();

        for (int fundingIndex = 0; fundingIndex < fundings.size(); fundingIndex++) {
            Funding funding = fundings.get(fundingIndex);

            for (int memberIndex = 0; memberIndex < members.size(); memberIndex++) {
                if (fundingIndex == memberIndex) {
                    continue;
                }

                int contributorPrice = 3_000 + ((fundingIndex + memberIndex) % 4) * 1_000;
                contributors.add(Contributor.createContributor(contributorPrice, members.get(memberIndex), funding));
            }
        }

        contributorRepository.saveAll(contributors);
    }

    private List<List<Item>> buildBookmarkPools(List<Item> items) {
        if (items.isEmpty()) {
            log.info("item 데이터가 아직 비어 있어 테스트 데이터 생성을 대기합니다.");
            return null;
        }

        LinkedHashMap<String, List<Item>> categoryMap = items.stream()
                .filter(item -> item.getCategory() != null && !item.getCategory().isBlank())
                .collect(Collectors.groupingBy(
                        Item::getCategory,
                        LinkedHashMap::new,
                        Collectors.toCollection(ArrayList::new)
                ));

        List<List<Item>> bookmarkPools = categoryMap.values().stream()
                .filter(categoryItems -> categoryItems.size() >= 2)
                .limit(5)
                .toList();

        if (bookmarkPools.size() < 5) {
            log.info("카테고리별 2개 아이템 구성을 위해 5개 카테고리 이상이 필요합니다. 현재 준비된 카테고리 수={}",
                    bookmarkPools.size());
            return null;
        }

        return bookmarkPools;
    }

    private record SeedMemberDefinition(
            String name,
            String email,
            MemberGender gender,
            String kakaoId,
            String profileImgUrl
    ) {
    }
}
