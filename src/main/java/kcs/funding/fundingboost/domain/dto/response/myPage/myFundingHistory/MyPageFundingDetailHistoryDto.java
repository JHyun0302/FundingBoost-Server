package kcs.funding.fundingboost.domain.dto.response.myPage.myFundingHistory;

import java.time.LocalDateTime;
import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record MyPageFundingDetailHistoryDto(
        Long fundingId,
        LocalDateTime createdDate,
        LocalDateTime deadLine,
        String itemImageUrl,
        String optionName,
        boolean status,
        Long contributorCount,
        String tag
) {
    public static MyPageFundingDetailHistoryDto fromEntity(Funding funding, Long contributor) {
        return MyPageFundingDetailHistoryDto.builder()
                .fundingId(funding.getFundingId())
                .createdDate(funding.getCreatedDate())
                .deadLine(funding.getDeadline())
                .itemImageUrl(funding.getFundingItems().get(0).getItem().getItemImageUrl())
                .optionName(funding.getFundingItems().get(0).getItem().getOptionName())
                .status(funding.getFundingItems().get(0).getFunding().isFundingStatus())
                .contributorCount(contributor)
                .tag(funding.getTag().getDisplayName())
                .build();
    }
}
