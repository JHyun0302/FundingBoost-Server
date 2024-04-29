package kcs.funding.fundingboost.domain.dto.response;


import kcs.funding.fundingboost.domain.entity.Item;
import lombok.Builder;

@Builder
public record WishtListItemDto(
        Long itemId,
        String itemThumbnailImageUrl,
        String itemName,
        int itemPrice) {

    public static WishtListItemDto fromEntity(Item item) {
        return WishtListItemDto.builder()
                .itemId(item.getItemId())
                .itemThumbnailImageUrl(item.getItemImageUrl())
                .itemName(item.getItemName())
                .itemPrice(item.getItemPrice())
                .build();
    }


}
