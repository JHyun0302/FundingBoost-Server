package kcs.funding.fundingboost.domain.dto.response;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record MyFundingStatusDto(Long fundingId,
                                 int fundingPercent,
                                 String deadline) {

    public static MyFundingStatusDto fromEntity(Funding funding, int currentPercent, String deadline) {
        return MyFundingStatusDto.builder()
            .fundingId(funding.getFundingId())
            .fundingPercent(currentPercent)
            .deadline(deadline)
            .build();
    }

}
