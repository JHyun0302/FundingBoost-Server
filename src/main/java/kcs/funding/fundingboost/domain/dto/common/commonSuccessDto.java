package kcs.funding.fundingboost.domain.dto.common;

import lombok.Builder;

@Builder
public record commonSuccessDto(boolean isSuccess) {
    public static commonSuccessDto fromEntity(boolean isSuccess) {
        return commonSuccessDto.builder()
                .isSuccess(isSuccess)
                .build();
    }
}
