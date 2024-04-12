package kcs.funding.fundingboost.domain.repository.relationship;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelationshipRepositoryRepository extends JpaRepository<Relationship, Long>,
    RelationshipRepositoryCustom {

    @Query("select r.friend from Relationship r"
        + " join fetch Member m"
        + " where r.member.memberId=:memberId")
    List<Member> findFriendByMemberId(@Param("memberId")Long memberId);
}
