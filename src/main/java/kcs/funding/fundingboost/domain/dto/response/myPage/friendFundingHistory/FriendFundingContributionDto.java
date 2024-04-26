package kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory;

import java.time.LocalDateTime;
import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Tag;
import lombok.Builder;

@Builder
public record FriendFundingContributionDto(String nickname,
                                           int price,
                                           String itemImageUrl,
                                           Tag tag,
                                           LocalDateTime createdDate) {
    public static FriendFundingContributionDto fromEntity(Contributor contributor, Funding friendFunding) {
        return FriendFundingContributionDto.builder()
                .nickname(friendFunding.getMember().getNickName())
                .price(contributor.getContributorPrice())
                .itemImageUrl(friendFunding.getMember().getProfileImgUrl())
                .tag(friendFunding.getTag())
                .createdDate(friendFunding.getCreatedDate())
                .build();
    }

}
