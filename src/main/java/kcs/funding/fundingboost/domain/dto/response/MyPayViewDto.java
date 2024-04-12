package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
public record MyPayViewDto(List<ItemDto> itemListDto,
                           List<DeliveryDto> deliveryListDto,
                           int point,
                           int totalPrice){

    public static MyPayViewDto fromEntity(List<ItemDto> itemListDto,
                                          List<DeliveryDto> deliveryListDto,
                                          int point,
                                          int totalPrice) {
        return MyPayViewDto.builder()
                .itemListDto(itemListDto)
                .deliveryListDto(deliveryListDto)
                .point(point)
                .totalPrice(totalPrice)
                .build();
    }
}
