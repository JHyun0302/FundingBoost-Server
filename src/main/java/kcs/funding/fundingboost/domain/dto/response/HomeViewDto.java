package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record HomeViewDto(HomeMemberInfoDto homeMemberInfoDto,
                          HomeMyFundingStatusDto homeMyFundingStatusDto,
                          List<HomeMyFundingItemDto> homeMyFundingItemDtoList,
                          List<HomeFriendFundingDto> homeFriendFundingDtoList,
                          List<ItemDto> itemDtoList) {

    public static HomeViewDto fromEntity(
        HomeMemberInfoDto homeMemberInfoDto,
        HomeMyFundingStatusDto homeMyFundingStatusDto,
        List<HomeMyFundingItemDto> homeMyFundingItemDtoList,
        List<HomeFriendFundingDto> homeFriendFundingDtoList,
        List<ItemDto> itemDtoList) {

        return HomeViewDto.builder()
            .homeMemberInfoDto(homeMemberInfoDto)
            .homeMyFundingStatusDto(homeMyFundingStatusDto)
            .homeMyFundingItemDtoList(homeMyFundingItemDtoList)
            .homeFriendFundingDtoList(homeFriendFundingDtoList)
            .itemDtoList(itemDtoList)
            .build();
    }
}
