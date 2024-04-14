package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundingItemRepository extends JpaRepository<FundingItem, Long> {
    List<FundingItem> findAllByFunding(Funding funding);
}
