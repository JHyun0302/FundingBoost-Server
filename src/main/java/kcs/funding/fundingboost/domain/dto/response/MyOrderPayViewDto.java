package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.request.OrderItemsDto;
import lombok.Builder;

@Builder
public record MyOrderPayViewDto(List<ItemDto> itemListDto,
                                List<Long> giftHubItemIds,
                                List<DeliveryDto> deliveryListDto,
                                int point
) {

    public static MyOrderPayViewDto fromEntity(
            OrderItemsDto orderItemsDtos,
            List<DeliveryDto> deliveryListDto,
            int point) {
        return MyOrderPayViewDto.builder()
                .itemListDto(orderItemsDtos.itemDtos())
                .giftHubItemIds(orderItemsDtos.giftHubItemIds())
                .deliveryListDto(deliveryListDto)
                .point(point)
                .build();
    }

}
