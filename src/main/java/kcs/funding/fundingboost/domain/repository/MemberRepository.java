package kcs.funding.fundingboost.domain.repository;

import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickName(String nickName);

    Optional<Member> findByEmail(String email);

    @Query("select m from Member m" +
            " where m.kakaoId in :kakaoIds")
    Optional<List<Member>> findAllByKakaoId(@Param("kakaoIds") List<String> kakaoIds);
}
