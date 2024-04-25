package kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory;

import java.time.LocalDate;
import kcs.funding.fundingboost.domain.entity.OrderItem;
import lombok.Builder;

@Builder
public record OrderHistoryItemDto(
        String itemName,
        String itemImageUrl,
        String optionName,
        int quantity,
        int price,
        LocalDate createdDate
) {

    public static OrderHistoryItemDto fromEntity(OrderItem orderItem) {
        return OrderHistoryItemDto.builder()
                .itemName(orderItem.getItem().getItemName())
                .itemImageUrl(orderItem.getItem().getItemImageUrl())
                .optionName(orderItem.getItem().getOptionName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getItem().getItemPrice() * orderItem.getQuantity()) // 총 가격을 반환
                .createdDate(LocalDate.from(orderItem.getOrder().getCreatedDate()))
                .build();
    }
}
