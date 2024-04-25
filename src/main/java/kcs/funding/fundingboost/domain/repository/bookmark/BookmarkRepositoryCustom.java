package kcs.funding.fundingboost.domain.repository.bookmark;

import kcs.funding.fundingboost.domain.entity.Bookmark;

public interface BookmarkRepositoryCustom {

    Bookmark findBookmarkByMemberAndItem(Long memberId, Long itemId);
}
