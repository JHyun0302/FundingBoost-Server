package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FundingItemRepository extends JpaRepository<FundingItem, Long> {

    @Query("select i from FundingItem i" +
            " join fetch i.funding f" +
            " where f.fundingId = :fundingId")
    List<FundingItem> findAllByFundingId(@Param("fundingId") Long fundingId);
}
