package kcs.funding.fundingboost.domain.dto.response;


import java.util.List;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import lombok.Builder;

@Builder
public record MyFundingPayViewDto(ItemDto itemDto,
                                  List<DeliveryDto> deliveryDtoList,
                                  int point,
                                  int collectPrice) {
    public static MyFundingPayViewDto fromEntity(FundingItem fundingItem,
                                                 List<DeliveryDto> deliveryDtoList) {

        Item item = fundingItem.getItem();
        return MyFundingPayViewDto.builder()
                .itemDto(ItemDto.fromEntity(item.getItemId(), item.getItemImageUrl(),
                        item.getItemName(), item.getOptionName(),
                        item.getItemPrice()))
                .deliveryDtoList(deliveryDtoList)
                .point(fundingItem.getFunding().getMember().getPoint())
                .collectPrice(fundingItem.getFunding().getCollectPrice())
                .build();
    }
}
