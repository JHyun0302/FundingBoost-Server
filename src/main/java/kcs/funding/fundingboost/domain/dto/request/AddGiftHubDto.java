package kcs.funding.fundingboost.domain.dto.request;

import lombok.Builder;

@Builder
public record AddGiftHubDto(Long memberId, int quantity) {
    public static AddGiftHubDto fromEntity(Long memberId, int quantity) {
        return AddGiftHubDto.builder()
                .memberId(memberId)
                .quantity(quantity)
                .build();
    }
}
