package kcs.funding.fundingboost.domain.dto.response.myPage.wishList;


import kcs.funding.fundingboost.domain.entity.Item;
import lombok.Builder;

@Builder
public record BookmarkListItemDto(
        Long itemId,
        String itemThumbnailImageUrl,
        String itemName,
        int itemPrice) {

    public static BookmarkListItemDto fromEntity(Item item) {
        return BookmarkListItemDto.builder()
                .itemId(item.getItemId())
                .itemThumbnailImageUrl(item.getItemImageUrl())
                .itemName(item.getItemName())
                .itemPrice(item.getItemPrice())
                .build();
    }


}
