package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record HomeFriendFundingDto(String nickName,
                                   String profile,
                                   Long fundingId,
                                   String nowFundingItemImageUrl,
                                   int friendFundingPercent,
                                   String friendFundingDeadlineDate) {

    public static HomeFriendFundingDto fromEntity(Funding funding, String nowFundingItemImageUrl,
        int currentFriendFundingPercent,
        String friendFundingDeadline) {

        return HomeFriendFundingDto.builder()
            .nickName(funding.getMember().getNickName())
            .profile(funding.getMember().getProfileImgUrl())
            .fundingId(funding.getFundingId())
            .nowFundingItemImageUrl(nowFundingItemImageUrl)
            .friendFundingDeadlineDate(friendFundingDeadline)
            .build();
    }
}
