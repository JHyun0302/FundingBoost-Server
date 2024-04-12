package kcs.funding.fundingboost.domain.repository.funding;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;

public interface FundingRepositoryCustom {

    List<Funding> findAllByMemberIn(List<Long> members);
}
