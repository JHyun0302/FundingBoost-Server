package kcs.funding.fundingboost.domain.dto.response;

import lombok.Builder;

@Builder
public record ItemDto(
        Long itemId,
        String itemThumbnailImageUrl,
        String itemName,
        String itemOption,
        int itemPrice,
        int quantity) {


    public static ItemDto fromEntity(
            Long itemId,
            String itemThumbnailImageUrl,
            String itemName,
            String itemOption,
            int itemPrice
    ) {
        return ItemDto.builder()
                .itemId(itemId)
                .itemThumbnailImageUrl(itemThumbnailImageUrl)
                .itemName(itemName)
                .itemOption(itemOption)
                .itemPrice(itemPrice)
                .quantity(1)
                .build();
    }
}
