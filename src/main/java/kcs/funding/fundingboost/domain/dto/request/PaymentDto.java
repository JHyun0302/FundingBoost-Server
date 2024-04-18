package kcs.funding.fundingboost.domain.dto.request;

import lombok.Builder;

@Builder
public record PaymentDto(int usingPoint) {
    public static PaymentDto fromEntity(int usingPoint){
        return PaymentDto.builder()
                .usingPoint(usingPoint)
                .build();
    }
}
