package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Item;
import lombok.Builder;

@Builder
public record ViewItemDto(Long itemId,
                          String itemName,
                          int price,
                          String itemImageUrl,
                          String brandName) {

    public static ViewItemDto fromEntity(Item item) {
        return ViewItemDto.builder()
            .itemId(item.getItemId())
            .itemName(item.getItemName())
            .price(item.getItemPrice())
            .itemImageUrl(item.getItemImageUrl())
            .brandName(item.getBrandName())
            .build();
    }
}
