package kcs.funding.fundingboost.domain.dto.response;

import java.time.LocalDateTime;
import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record MyFundingResponseDto(
        Long fundingId,
        LocalDateTime createdDate,
        LocalDateTime deadLine,
        String itemImageUrl,
        String optionName,
        boolean status,
        Long contributor,
        String tag
) {
    public static MyFundingResponseDto fromEntity(Funding funding, Long contributor) {
        return MyFundingResponseDto.builder()
                .fundingId(funding.getFundingId())
                .createdDate(funding.getCreatedDate())
                .deadLine(funding.getDeadline())
                .itemImageUrl(funding.getFundingItems().get(0).getItem().getItemImageUrl())
                .optionName(funding.getFundingItems().get(0).getItem().getOptionName())
                .status(funding.getFundingItems().get(0).getFunding().isFundingStatus())
                .contributor(contributor)
                .tag(funding.getTag().getDisplayName())
                .build();
    }
}
