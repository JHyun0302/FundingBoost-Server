package kcs.funding.fundingboost.domain.dto.response;

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
}
