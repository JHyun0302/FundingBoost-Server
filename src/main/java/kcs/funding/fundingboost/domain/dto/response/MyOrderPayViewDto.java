package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MyOrderPayViewDto(List<ItemDto> itemListDto,
                                List<Long> giftHubItemIds,
                                List<DeliveryDto> deliveryListDto,
                                int point
) {

    public static MyOrderPayViewDto fromEntity(
            List<ItemDto> itemListDto,
            List<Long> giftHubItemIds,
            List<DeliveryDto> deliveryListDto,
            int point) {
        return MyOrderPayViewDto.builder()
                .itemListDto(itemListDto)
                .giftHubItemIds(giftHubItemIds)
                .deliveryListDto(deliveryListDto)
                .point(point)
                .build();
    }

}
