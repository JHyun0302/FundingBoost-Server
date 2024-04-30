package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MyWishListDto(MyPageMemberDto myPageMemberDto, List<WishtListItemDto> wishtListItemDtos) {

    public static MyWishListDto fromEntity(MyPageMemberDto myPageMemberDto, List<WishtListItemDto> wishtListItemDtos) {
        return MyWishListDto.builder()
                .myPageMemberDto(myPageMemberDto)
                .wishtListItemDtos(wishtListItemDtos)
                .build();
    }

}
