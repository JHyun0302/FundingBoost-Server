package kcs.funding.fundingboost.domain.dto.request;

import lombok.Builder;

@Builder
public record PaymentDto(Long deliveryId, int usingPoint) {
    public static PaymentDto fromEntity(Long deliveryId, int usingPoint){
        return PaymentDto.builder()
                .deliveryId(deliveryId)
                .usingPoint(usingPoint)
                .build();
    }

}
