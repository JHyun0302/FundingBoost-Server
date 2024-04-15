package kcs.funding.fundingboost.domain.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record MyPayViewDto(List<ItemDto> itemListDto,
                           List<DeliveryDto> deliveryListDto,
                           int point,
                           int collectPrice){

    public static MyPayViewDto fromEntity(List<ItemDto> itemListDto,
                                          List<DeliveryDto> deliveryListDto,
                                          int point) {
        return MyPayViewDto.builder()
                .itemListDto(itemListDto)
                .deliveryListDto(deliveryListDto)
                .point(point)
                .build();
    }

    public static MyPayViewDto fromEntity(List<ItemDto> itemListDto,
                                          List<DeliveryDto> deliveryListDto,
                                          int point,
                                          int collectPrice) {
        return MyPayViewDto.builder()
                .itemListDto(itemListDto)
                .deliveryListDto(deliveryListDto)
                .point(point)
                .collectPrice(collectPrice)
                .build();
    }
}
