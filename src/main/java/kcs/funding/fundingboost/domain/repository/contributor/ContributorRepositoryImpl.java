package kcs.funding.fundingboost.domain.repository.contributor;

import static kcs.funding.fundingboost.domain.entity.QContributor.contributor;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ContributorRepositoryImpl implements ContributorRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long countContributorsForFunding(Long fundingId) {
        return queryFactory
                .select(contributor.count())
                .from(contributor)
                .where(contributor.funding.fundingId.eq(fundingId))
                .fetchOne();
    }
}
