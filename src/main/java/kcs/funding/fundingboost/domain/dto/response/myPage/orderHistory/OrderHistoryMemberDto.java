package kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory;

import kcs.funding.fundingboost.domain.entity.Member;
import lombok.Builder;

@Builder
public record OrderHistoryMemberDto(
        String nickName,
        String email,
        String profile,
        int point
) {

    public static OrderHistoryMemberDto fromEntity(Member member) {
        return OrderHistoryMemberDto.builder()
                .nickName(member.getNickName())
                .email(member.getEmail())
                .profile(member.getProfileImgUrl())
                .point(member.getPoint())
                .build();
    }
}
