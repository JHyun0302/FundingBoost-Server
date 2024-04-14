package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<Funding, Long> {

    Funding findByMemberAndFundingStatus(Member member, boolean b);
}
