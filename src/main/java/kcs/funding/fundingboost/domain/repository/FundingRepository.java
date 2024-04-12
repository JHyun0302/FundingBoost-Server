package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<Funding, Long> {
}
