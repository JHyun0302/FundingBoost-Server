package kcs.funding.fundingboost.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "member"
)
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @NotNull
    @Column(name = "nick_name", length = 20)
    private String nickName;

    @NotNull
    @Column(length = 50)
    private String email;

    @NotNull
    @Column(name = "profile_img_url", length = 100)
    private String profileImgUrl;

    @ColumnDefault("0")
    @NotNull
    private int point;

    public static Member createMember(String nickName, String email, String profileImgUrl, int point) {
        Member member = new Member();
        member.nickName = nickName;
        member.email = email;
        member.profileImgUrl = profileImgUrl;
        member.point = point;
        return member;
    }
}
