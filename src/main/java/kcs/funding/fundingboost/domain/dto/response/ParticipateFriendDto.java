package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Member;
import lombok.Builder;

@Builder
public record ParticipateFriendDto(
        String participateNickname,
        int participatePrice,
        String participateProfileImgUrl
) {
    public static ParticipateFriendDto fromEntity(
            Contributor contributor
    ){
        return ParticipateFriendDto.builder()
                .participateNickname(contributor.getMember().getNickName())
                .participatePrice(contributor.getContributorPrice())
                .participateProfileImgUrl(contributor.getMember().getProfileImgUrl())
                .build();
    }
}
