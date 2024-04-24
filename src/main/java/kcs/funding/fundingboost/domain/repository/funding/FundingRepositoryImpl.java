package kcs.funding.fundingboost.domain.repository.funding;

import static kcs.funding.fundingboost.domain.entity.QContributor.contributor;
import static kcs.funding.fundingboost.domain.entity.QFunding.funding;
import static kcs.funding.fundingboost.domain.entity.QFundingItem.fundingItem;
import static kcs.funding.fundingboost.domain.entity.QItem.item;
import static kcs.funding.fundingboost.domain.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FundingRepositoryImpl implements FundingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Funding> findAllByMemberIn(List<Long> memberIds) {
        log.info("FundingRepositoryImpl findAllByMemberIn called");
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
                .join(funding.fundingItems, fundingItem)
                .join(fundingItem.item, item)
                .join(funding.member, member).fetchJoin()
                .where(funding.member.memberId.eq(memberId)
                        .and(funding.fundingStatus.eq(true)))
                .fetchOne();
    }

    @Override
    public Funding findMemberByFundingId(Long fundingId) {
        return queryFactory
                .selectFrom(funding)
                .join(funding.member, member).fetchJoin()
                .where(funding.fundingId.eq(fundingId))
                .fetchOne();
    }

    @Override
    public List<Funding> findFundingByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(funding)
                .join(funding.member, member).fetchJoin()
                .where(funding.member.memberId.eq(memberId))
                .orderBy(funding.createdDate.desc())
                .fetch();
    }

    @Override
    public Long countContributorsForFunding(Long fundingId) {
        return queryFactory
                .select(contributor.count())
                .from(contributor)
                .where(contributor.funding.fundingId.eq(fundingId))
                .fetchOne();
    }
}
