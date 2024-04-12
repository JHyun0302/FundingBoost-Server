package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Member;
import lombok.Builder;

@Builder
public record MemberDto(String nickName, String profile) {

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
            .nickName(member.getNickName())
            .profile(member.getProfileImgUrl())
            .build();
    }
}
