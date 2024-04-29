package kcs.funding.fundingboost.domain.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MyWishListDto(MyPageMemberDto myPageMemberDto, List<ItemDto> itemDtoList) {

    public static MyWishListDto fromEntity(MyPageMemberDto myPageMemberDto, List<ItemDto> itemDtoList) {
        return MyWishListDto.builder()
                .myPageMemberDto(myPageMemberDto)
                .itemDtoList(itemDtoList)
                .build();
    }

}
