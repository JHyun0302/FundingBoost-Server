package kcs.funding.fundingboost.domain.dto.request;

import lombok.Builder;

@Builder
public record MyPayDto(int usingPoint) {
    public static MyPayDto fromEntity(int usingPoint) {
        return MyPayDto.builder()
                .usingPoint(usingPoint)
                .build();
    }
}
