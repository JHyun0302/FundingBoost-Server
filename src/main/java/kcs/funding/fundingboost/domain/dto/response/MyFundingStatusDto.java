package kcs.funding.fundingboost.domain.dto.response;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record MyFundingStatusDto(Long fundingId,
                                 String deadline) {

    public static MyFundingStatusDto fromEntity(Funding funding, String deadline) {
        return MyFundingStatusDto.builder()
            .fundingId(funding.getFundingId())
            .deadline(deadline)
            .build();
    }

}
