package kcs.funding.fundingboost.domain.repository.relationship;

import java.util.List;

public interface RelationshipRepositoryCustom {

    List<Long> findFriendIdByMemberId(Long memberId);
}
