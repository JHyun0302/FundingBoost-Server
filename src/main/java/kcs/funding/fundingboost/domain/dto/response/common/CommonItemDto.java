package kcs.funding.fundingboost.domain.dto.response.common;

import lombok.Builder;

@Builder
public record CommonItemDto(
        Long itemId,
        String itemThumbnailImageUrl,
        String itemName,
        String itemOption,
        int itemPrice,
        int quantity) {

    public static CommonItemDto fromEntity(
            Long itemId,
            String itemThumbnailImageUrl,
            String itemName,
            String itemOption,
            int itemPrice
    ) {
        return CommonItemDto.builder()
                .itemId(itemId)
                .itemThumbnailImageUrl(itemThumbnailImageUrl)
                .itemName(itemName)
                .itemOption(itemOption)
                .itemPrice(itemPrice)
                .quantity(1)
                .build();
    }
}
