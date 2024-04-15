package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Order;
import lombok.Builder;

@Builder
public record ItemDto(String itemThumnailImageUrl,
                          String itemName,
                          String itemOption,
                          int itemPrice,
                          int quantity) {

    public static ItemDto fromEntity(Order order) {
        return ItemDto.builder()
                .itemThumnailImageUrl(order.getItem().getItemImageUrl())
                .itemName(order.getItem().getItemName())
                .itemOption(order.getItem().getOptionName())
                .itemPrice(order.getItem().getItemPrice())
                .quantity(order.getQuantity())
                .build();
    }
    public static ItemDto fromEntity(FundingItem fundingItem) {
        return ItemDto.builder()
                .itemThumnailImageUrl(fundingItem.getItem().getItemImageUrl())
                .itemName(fundingItem.getItem().getItemName())
                .itemOption(fundingItem.getItem().getOptionName())
                .itemPrice(fundingItem.getItem().getItemPrice())
                .quantity(1)
                .build();
    }
}
