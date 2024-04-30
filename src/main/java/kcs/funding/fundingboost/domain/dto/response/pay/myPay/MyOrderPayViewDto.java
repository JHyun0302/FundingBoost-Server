package kcs.funding.fundingboost.domain.dto.response.pay.myPay;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.common.CommonItemDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.DeliveryDto;
import lombok.Builder;

@Builder
public record MyOrderPayViewDto(List<CommonItemDto> itemListDto,
                                List<Long> giftHubItemIds,
                                List<DeliveryDto> deliveryListDto,
                                int point
) {

    public static MyOrderPayViewDto fromEntity(
            List<CommonItemDto> itemListDto,
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
