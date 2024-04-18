package kcs.funding.fundingboost.domain.repository.relationship;

import static kcs.funding.fundingboost.domain.entity.QMember.member;
import static kcs.funding.fundingboost.domain.entity.QRelationship.relationship;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RelationshipRepositoryRepositoryImpl implements RelationshipRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findFriendIdByMemberId(Long memberId) {
        return queryFactory
                .select(relationship.friend.memberId)
                .from(relationship)
                .leftJoin(relationship.member, member).fetchJoin()
                .where(relationship.member.memberId.eq(memberId))
                .fetch();
    }
}
