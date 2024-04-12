package kcs.funding.fundingboost.domain.dto.response;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record MyFundingStatusDto(Long fundingId,
                                 float fundingPercent,
                                 String deadline) {

    public static MyFundingStatusDto fromEntity(Funding funding) {
        float currentPercent = (float) funding.getCollectPrice() / funding.getTotalPrice();
        int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(), funding.getDeadline());
        String deadline = "D-" + leftDate;

        return MyFundingStatusDto.builder()
            .fundingId(funding.getFundingId())
            .fundingPercent(currentPercent)
            .deadline(deadline)
            .build();
    }

}
