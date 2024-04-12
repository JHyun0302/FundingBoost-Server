package kcs.funding.fundingboost.domain.repository.funding;

import static kcs.funding.fundingboost.domain.entity.QFunding.funding;
import static kcs.funding.fundingboost.domain.entity.QFundingItem.fundingItem;
import static kcs.funding.fundingboost.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.QItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FundingRepositoryImpl implements FundingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Funding> findAllByMemberIn(List<Long> memberIds) {
        return queryFactory
            .selectFrom(funding)
            .leftJoin(funding.member, member).fetchJoin()
            .leftJoin(funding.fundingItems, fundingItem).fetchJoin()
            .leftJoin(fundingItem.item, QItem.item).fetchJoin()
            .where(member.memberId.in(memberIds))
            .fetch();
    }
}
