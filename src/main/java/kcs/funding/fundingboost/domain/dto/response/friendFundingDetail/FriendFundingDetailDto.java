package kcs.funding.fundingboost.domain.dto.response.friendFundingDetail;


import java.time.LocalDateTime;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.Builder;

@Builder
public record FriendFundingDetailDto(List<FriendFundingItemDto> friendFundingItemList,
                                     List<ContributorDto> contributorList,
                                     String friendName, String friendProfile,
                                     LocalDateTime deadline, int contributedPercent,
                                     String fundingTag, String fundingMessage) {


    public static FriendFundingDetailDto fromEntity(List<FriendFundingItemDto> friendFundingItemList, Funding funding,
                                                    List<ContributorDto> contributorList, int contributedPercent
    ) {

        return FriendFundingDetailDto.builder()
                .friendFundingItemList(friendFundingItemList)
                .friendName(funding.getMember().getNickName())
                .fundingTag(funding.getTag().getDisplayName())
                .fundingMessage(funding.getMessage())
                .friendProfile(funding.getMember().getProfileImgUrl())
                .contributorList(contributorList)
                .deadline(funding.getDeadline())
                .contributedPercent(contributedPercent)
                .build();
    }
}
