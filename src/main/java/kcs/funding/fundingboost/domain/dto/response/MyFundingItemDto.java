package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.FundingItem;
import lombok.Builder;

@Builder
public record MyFundingItemDto(String itemImageUrl,
                               int itemPercent) {

    public static MyFundingItemDto fromEntity(FundingItem fundingItem, int itemPercent) {
        return MyFundingItemDto.builder()
            .itemImageUrl(fundingItem.getItem().getItemImageUrl())
            .itemPercent(itemPercent)
            .build();
    }
}
