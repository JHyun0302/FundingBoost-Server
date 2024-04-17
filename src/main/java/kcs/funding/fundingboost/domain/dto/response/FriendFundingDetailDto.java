package kcs.funding.fundingboost.domain.dto.response;


import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Tag;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record FriendFundingDetailDto(List<FriendFundingItemDto> friendFundingItemList, List<ContributorDto> contributorList,
                                    String friendName, String friendProfile,
                                    LocalDateTime deadline, int contributedPercent,
                                    Tag fundingTag, String fundingMessage) {


    public static FriendFundingDetailDto fromEntity(List<FriendFundingItemDto> friendFundingItemList, Funding funding,
                                                    List<ContributorDto> contributorList, int contributedPercent
    ) {

        return FriendFundingDetailDto.builder()
                .friendFundingItemList(friendFundingItemList)
                .friendName(funding.getMember().getNickName())
                .fundingTag(funding.getTag())
                .fundingMessage(funding.getMessage())
                .friendProfile(funding.getMember().getProfileImgUrl())
                .contributorList(contributorList)
                .deadline(funding.getDeadline())
                .contributedPercent(contributedPercent)
                .build();
    }
}
