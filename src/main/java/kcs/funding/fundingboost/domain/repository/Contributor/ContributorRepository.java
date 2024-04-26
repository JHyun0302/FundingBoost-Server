package kcs.funding.fundingboost.domain.repository.Contributor;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContributorRepository extends JpaRepository<Contributor, Long>, ContributorRepositoryCustom {

    @Query("select c from Contributor c" +
            " join fetch c.funding f" +
            " where f.fundingId = :fundingId")
    List<Contributor> findByFundingId(@Param("fundingId") Long fundingId);

    @Query("select c from Contributor c" +
            " join fetch c.funding f" +
            " join fetch c.member m" +
            " where f.fundingId = :fundingId")
    List<Contributor> findAllByFundingId(@Param("fundingId") Long fundingId);


    @Query("select c from Contributor c" +
            " join fetch c.funding f" +
            " join fetch c.member m" +
            " join fetch f.fundingItems fi" +
            " join fetch fi.item" +
            " where c.member.memberId = :memberId")
    List<Contributor> findAllByMemberId(@Param("memberId") Long memberId);
}
