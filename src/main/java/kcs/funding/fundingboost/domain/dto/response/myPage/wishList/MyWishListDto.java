package kcs.funding.fundingboost.domain.dto.response.myPage.wishList;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import lombok.Builder;

@Builder
public record MyWishListDto(MyPageMemberDto myPageMemberDto, List<WishListItemDto> wishListItemDtos) {

    public static MyWishListDto fromEntity(MyPageMemberDto myPageMemberDto, List<WishListItemDto> wishListItemDtos) {
        return MyWishListDto.builder()
                .myPageMemberDto(myPageMemberDto)
                .wishListItemDtos(wishListItemDtos)
                .build();
    }

}
