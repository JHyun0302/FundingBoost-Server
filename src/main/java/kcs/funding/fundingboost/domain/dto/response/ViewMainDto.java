package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ViewMainDto(MemberDto memberDto,
                          MyFundingStatusDto myFundingStatusDto,
                          List<MyFundingItemDto> myFundingItemListDto,
                          List<FriendFundingDto> friendFundingListDto,
                          List<ViewItemDto> viewItemListDto) {

    public static ViewMainDto fromEntity(
        MemberDto memberDto,
        MyFundingStatusDto myFundingStatusDto,
        List<MyFundingItemDto> myFundingItemListDto,
        List<FriendFundingDto> friendFundingListDto,
        List<ViewItemDto> viewItemListDto) {

        return ViewMainDto.builder()
            .memberDto(memberDto)
            .myFundingStatusDto(myFundingStatusDto)
            .myFundingItemListDto(myFundingItemListDto)
            .friendFundingListDto(friendFundingListDto)
            .viewItemListDto(viewItemListDto)
            .build();
    }
}
