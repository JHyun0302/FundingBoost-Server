package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Order;
import lombok.Builder;

@Builder
public record MyPayViewDto(List<ItemDto> itemListDto,
                           List<DeliveryDto> deliveryListDto,
                           int point,
                           int collectPrice) {

    public static MyPayViewDto fromEntity(
        List<ItemDto> itemListDto,
        List<DeliveryDto> deliveryListDto,
        Order order) {
        return MyPayViewDto.builder()
            .itemListDto(itemListDto)
            .deliveryListDto(deliveryListDto)
            .point(order.getMember().getPoint())
            .build();
    }

    public static MyPayViewDto fromEntity(
        List<ItemDto> itemListDto,
        List<DeliveryDto> deliveryListDto,
        Funding funding) {
        return MyPayViewDto.builder()
            .itemListDto(itemListDto)
            .deliveryListDto(deliveryListDto)
            .point(funding.getMember().getPoint())
            .collectPrice(funding.getCollectPrice())
            .build();
    }
}
