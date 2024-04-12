package kcs.funding.fundingboost.domain.repository.funding;

import kcs.funding.fundingboost.domain.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<Funding, Long>, FundingRepositoryCustom {

}
