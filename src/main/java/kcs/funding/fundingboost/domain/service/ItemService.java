package kcs.funding.fundingboost.domain.service;

import java.util.List;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.response.ItemDetailDto;
import kcs.funding.fundingboost.domain.dto.response.ShopDto;
import kcs.funding.fundingboost.domain.entity.Bookmark;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.bookmark.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookmarkRepository bookmarkRepository;

    public List<ShopDto> getItems() {
        List<Item> items = itemRepository.findAll();

        return items.stream()
                .map(ShopDto::createGiftHubDto)
                .collect(Collectors.toList());
    }

    public ItemDetailDto getItemDetail(Long memberId, Long itemId) {

        Bookmark bookmark = bookmarkRepository.findBookmarkByMemberAndItem(memberId, itemId);
        if (bookmark != null) {
            return ItemDetailDto.fromEntity(bookmark.getItem(), true);
        } else {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item Not Found"));

            return ItemDetailDto.fromEntity(item, false);
        }
    }
}
