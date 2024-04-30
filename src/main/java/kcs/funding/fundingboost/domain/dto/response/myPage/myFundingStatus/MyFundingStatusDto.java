package kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import lombok.Builder;

@Builder
public record MyFundingStatusDto(
        MyPageMemberDto myPageMemberDto,
        List<MyPageFundingItemDto> myPageFundingItemDtoList,
        List<ParticipateFriendDto> participateFriendDtoList,
        int totalPercent,
        String deadline,        // 2024.06.11
        String deadlineDate     //D-3
) {
    public static MyFundingStatusDto createMyFundingStatusDto(
            MyPageMemberDto myPageMemberDto,
            List<MyPageFundingItemDto> myPageFundingItemDtoList,
            List<ParticipateFriendDto> participateFriendDtoList,
            int totalPercent,
            String deadline,
            String deadlineDate
    ) {
        return MyFundingStatusDto.builder()
                .myPageMemberDto(myPageMemberDto)
                .myPageFundingItemDtoList(myPageFundingItemDtoList)
                .participateFriendDtoList(participateFriendDtoList)
                .totalPercent(totalPercent)
                .deadline(deadline)
                .deadlineDate(deadlineDate)
                .build();
    }

    public static MyFundingStatusDto createNotExistFundingMyFundingStatusDto(
            MyPageMemberDto myPageMemberDto
    ) {
        return MyFundingStatusDto.builder()
                .myPageMemberDto(myPageMemberDto)
                .build();
    }
}
