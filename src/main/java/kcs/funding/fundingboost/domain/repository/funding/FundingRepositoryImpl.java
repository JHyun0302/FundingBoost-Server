package kcs.funding.fundingboost.domain.repository.funding;

import static kcs.funding.fundingboost.domain.entity.QFunding.funding;
import static kcs.funding.fundingboost.domain.entity.QFundingItem.fundingItem;
import static kcs.funding.fundingboost.domain.entity.QItem.*;
import static kcs.funding.fundingboost.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.QFundingItem;
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
            .leftJoin(fundingItem.item, item).fetchJoin()
            .where(member.memberId.in(memberIds))
            .fetch();
    }

    @Override
    public Funding findFundingInfo(Long memberId) {
        return queryFactory
            .selectFrom(funding)
            .leftJoin(funding.fundingItems, fundingItem)
            .leftJoin(fundingItem.item, item).fetchJoin()
            .join(member).fetchJoin()
            .where(funding.member.memberId.eq(memberId))
            .fetchOne();
    }
}
