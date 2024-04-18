package kcs.funding.fundingboost.domain.dto.response;

import lombok.Builder;

@Builder
public record FriendFundingDto(
        CommonFriendFundingDto commonFriendFundingDto
) {
    public static FriendFundingDto fromEntity(
            CommonFriendFundingDto commonFriendFundingDto
    ) {
        return FriendFundingDto.builder()
                .commonFriendFundingDto(commonFriendFundingDto)
                .build();
    }
}