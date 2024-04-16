package kcs.funding.fundingboost.domain.dto.response;


import kcs.funding.fundingboost.domain.entity.Tag;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record FriendFundingDetailDto(List<FriendFundingItemDto> friendFundingItemList, List<ContributorDto> contributorList,
                                    String friendName, String friendProfile,
                                    LocalDateTime deadline, int contributedPercent,
                                    Tag fundingTag, String fundingMessage) {


    public static FriendFundingDetailDto fromEntity(List<FriendFundingItemDto> friendFundingItemList, List<ContributorDto> contributorList,
                                                    String friendName, String friendProfile,
                                                    LocalDateTime deadline, int contributedPercent,
                                                    Tag fundingTag, String fundingMessage) {
        return FriendFundingDetailDto.builder()
                .friendFundingItemList(friendFundingItemList)
                .friendName(friendName)
                .fundingTag(fundingTag)
                .fundingMessage(fundingMessage)
                .friendProfile(friendProfile)
                .contributorList(contributorList)
                .deadline(deadline)
                .contributedPercent(contributedPercent)
                .build();
    }
}
