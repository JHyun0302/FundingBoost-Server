package kcs.funding.fundingboost.domain.dto.response;


import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
import lombok.Builder;

@Builder
public record MyFundingPayViewDto(ItemDto item,
                                  List<DeliveryDto> deliveryDtoList,
                                  int point,
                                  int collectPrice) {
    public static MyFundingPayViewDto fromEntity(FundingItem fundingItem,
                                                 List<DeliveryDto> deliveryDtoList) {
        Funding funding = fundingItem.getFunding();

        if (!funding.isFundingStatus()) {
            throw new CommonException(ErrorCode.INVALID_FUNDING_STATUS);
        }
        return MyFundingPayViewDto.builder()
                .item(ItemDto.fromEntity(fundingItem))
                .deliveryDtoList(deliveryDtoList)
                .point(funding.getMember().getPoint())
                .collectPrice(funding.getCollectPrice())
                .build();
    }
}
