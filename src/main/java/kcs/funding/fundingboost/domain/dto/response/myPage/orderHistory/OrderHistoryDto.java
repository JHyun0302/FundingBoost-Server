package kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory;

import java.util.List;
import lombok.Builder;

@Builder
public record OrderHistoryDto(
        OrderHistoryMemberDto orderHistoryMemberDto,
        List<OrderHistoryItemDto> orderHistoryItemDtoList
) {

    public static OrderHistoryDto fromEntity(OrderHistoryMemberDto orderHistoryMemberDto,
                                             List<OrderHistoryItemDto> orderHistoryItemDtoList) {
        return OrderHistoryDto.builder()
                .orderHistoryMemberDto(orderHistoryMemberDto)
                .orderHistoryItemDtoList(orderHistoryItemDtoList)
                .build();
    }
}
