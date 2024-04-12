package kcs.funding.fundingboost.domain.repository.relationship;

import java.util.List;

public interface RelationshipCustom {

    List<Long> findFriendIdByMemberId(Long memberId);
}
