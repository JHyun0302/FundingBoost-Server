package kcs.funding.fundingboost.domain.repository.GiftHubItem;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;

public interface GiftHubItemRepositoryCustom {
    List<GiftHubItem> findGiftHubItemsByMember(Long memberId);
}
