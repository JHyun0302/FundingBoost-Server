package kcs.funding.fundingboost.domain.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record MyPayDto(List<ItemPayDto> itemPayDtoList, Long deliveryId, int usingPoint) {
    public static MyPayDto fromEntity(List<ItemPayDto> itemPayDtoList, Long deliveryId, int usingPoint) {
        return MyPayDto.builder()
                .itemPayDtoList(itemPayDtoList)
                .deliveryId(deliveryId)
                .usingPoint(usingPoint)
                .build();
    }
}
