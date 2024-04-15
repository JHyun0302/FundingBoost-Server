package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.FundingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingItemRepository extends JpaRepository<FundingItem, Long> {
}
