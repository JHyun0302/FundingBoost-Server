package kcs.funding.fundingboost.domain.dto.request;

import lombok.Builder;

@Builder
public record FundingPaymentDto(Long fundingItemId, int usingPoint) {
    public static FundingPaymentDto fromEntity(Long fundingItemId, int usingPoint) {
        return FundingPaymentDto.builder()
                .fundingItemId(fundingItemId)
                .usingPoint(usingPoint)
                .build();
    }
}
