package kcs.funding.fundingboost.domain.repository.funding;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;

public interface FundingRepositoryCustom {

    Funding findFundingInfo(Long memberId);

    Funding findMemberByFundingId(Long fundingId);

    List<Funding> findFundingByMemberId(Long memberId);
}
