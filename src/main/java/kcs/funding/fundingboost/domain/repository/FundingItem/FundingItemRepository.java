package kcs.funding.fundingboost.domain.repository.FundingItem;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingItemRepository extends JpaRepository<FundingItem, Long>, FundingItemRepositoryCustom {
    List<FundingItem> findAllByFunding(Funding funding);
}
