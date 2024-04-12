package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record FriendFundingDto(String nickName,
                               String profile,
                               Long fundingId,
                               String nowFundingItemImageUrl,
                               int friendFundingPercent,
                               String friendFundingDeadlineDate) {

    public static FriendFundingDto fromEntity(Funding funding, String nowFundingItemImageUrl,
        int currentFriendFundingPercent,
        String friendFundingDeadline) {

        return FriendFundingDto.builder()
            .nickName(funding.getMember().getNickName())
            .profile(funding.getMember().getProfileImgUrl())
            .fundingId(funding.getFundingId())
            .nowFundingItemImageUrl(nowFundingItemImageUrl)
            .friendFundingDeadlineDate(friendFundingDeadline)
            .build();
    }
}
