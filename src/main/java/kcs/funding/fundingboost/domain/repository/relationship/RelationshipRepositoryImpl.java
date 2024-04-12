package kcs.funding.fundingboost.domain.repository.relationship;

import static kcs.funding.fundingboost.domain.entity.QMember.*;
import static kcs.funding.fundingboost.domain.entity.QRelationship.*;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.QMember;
import kcs.funding.fundingboost.domain.entity.QRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RelationshipRepositoryImpl implements RelationshipCustom {

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
