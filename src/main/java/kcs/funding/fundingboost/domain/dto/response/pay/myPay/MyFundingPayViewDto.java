package kcs.funding.fundingboost.domain.dto.response.pay.myPay;


import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.common.CommonItemDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.DeliveryDto;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import lombok.Builder;

@Builder
public record MyFundingPayViewDto(CommonItemDto commonItemDto,
                                  List<DeliveryDto> deliveryDtoList,
                                  int point,
                                  int collectPrice) {
    public static MyFundingPayViewDto fromEntity(FundingItem fundingItem,
                                                 List<DeliveryDto> deliveryDtoList) {

        Item item = fundingItem.getItem();
        return MyFundingPayViewDto.builder()
                .commonItemDto(CommonItemDto.fromEntity(item.getItemId(), item.getItemImageUrl(),
                        item.getItemName(), item.getOptionName(),
                        item.getItemPrice()))
                .deliveryDtoList(deliveryDtoList)
                .point(fundingItem.getFunding().getMember().getPoint())
                .collectPrice(fundingItem.getFunding().getCollectPrice())
                .build();
    }
}
