package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MyNowOrderPayViewDto(ItemDto itemDto,
                                   List<DeliveryDto> deliveryDtoList,
                                   int point) {

    public static MyNowOrderPayViewDto fromEntity(ItemDto itemDto, List<DeliveryDto> deliveryDtoList, int point) {
        return MyNowOrderPayViewDto.builder()
                .itemDto(itemDto)
                .deliveryDtoList(deliveryDtoList)
                .point(point)
                .build();
    }
}
