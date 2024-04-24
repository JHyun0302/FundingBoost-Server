package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MyFundingHistoryDto(
        MyPageMemberDto myPageMemberDto,
        List<MyFundingResponseDto> myFundingResponseDtos
) {
    public static MyFundingHistoryDto fromEntity(MyPageMemberDto myPageMemberDto,
                                                 List<MyFundingResponseDto> myFundingResponseDtos) {
        return MyFundingHistoryDto.builder()
                .myPageMemberDto(myPageMemberDto)
                .myFundingResponseDtos(myFundingResponseDtos)
                .build();
    }
}
