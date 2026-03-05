package kcs.funding.fundingboost.domain.repository;

import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.entity.member.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickName(String nickName);

    Optional<Member> findByEmail(String email);

    long countByMemberRole(MemberRole memberRole);

    @Query(
            value = "select m from Member m " +
                    "where (:keyword is null or :keyword = '' " +
                    "or lower(m.email) like lower(concat('%', :keyword, '%')) " +
                    "or lower(m.nickName) like lower(concat('%', :keyword, '%'))) " +
                    "and (:role is null or m.memberRole = :role)",
            countQuery = "select count(m) from Member m " +
                    "where (:keyword is null or :keyword = '' " +
                    "or lower(m.email) like lower(concat('%', :keyword, '%')) " +
                    "or lower(m.nickName) like lower(concat('%', :keyword, '%'))) " +
                    "and (:role is null or m.memberRole = :role)"
    )
    Page<Member> searchForAdmin(@Param("keyword") String keyword,
                                @Param("role") MemberRole role,
                                Pageable pageable);

    @Query("select m from Member m order by m.createdDate desc")
    List<Member> findRecentMembers(Pageable pageable);

    @Query("select m from Member m" +
            " where m.kakaoId in :kakaoIds")
    Optional<List<Member>> findAllByKakaoId(@Param("kakaoIds") List<String> kakaoIds);
}
