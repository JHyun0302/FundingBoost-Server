package kcs.funding.fundingboost.domain.repository.FundingItem;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FundingItemRepository extends JpaRepository<FundingItem, Long> {

    @Query("select i from FundingItem i" +
            " join fetch i.funding f" +
            " where f.fundingId = :fundingId")
    List<FundingItem> findAllByFundingId(@Param("fundingId") Long fundingId);

    @Query("select fi from FundingItem fi" +
            " join fetch fi.funding f" +
            " where f.fundingId = :fundingId")
    List<FundingItem> findFundingItemIdListByFunding(@Param("fundingId") Long fundingId);
}