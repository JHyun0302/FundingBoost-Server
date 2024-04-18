package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record HomeMyFundingStatusDto(Long fundingId,
                                     String deadline) {

    public static HomeMyFundingStatusDto fromEntity(Funding funding, String deadline) {
        return HomeMyFundingStatusDto.builder()
                .fundingId(funding.getFundingId())
                .deadline(deadline)
                .build();
    }

}
