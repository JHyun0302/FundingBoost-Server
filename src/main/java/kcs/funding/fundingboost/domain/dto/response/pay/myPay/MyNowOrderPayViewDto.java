package kcs.funding.fundingboost.domain.dto.response.pay.myPay;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.common.CommonItemDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.DeliveryDto;
import lombok.Builder;

@Builder
public record MyNowOrderPayViewDto(CommonItemDto commonItemDto,
                                   List<DeliveryDto> deliveryDtoList,
                                   int point) {

    public static MyNowOrderPayViewDto fromEntity(CommonItemDto commonItemDto, List<DeliveryDto> deliveryDtoList,
                                                  int point) {
        return MyNowOrderPayViewDto.builder()
                .commonItemDto(commonItemDto)
                .deliveryDtoList(deliveryDtoList)
                .point(point)
                .build();
    }
}
