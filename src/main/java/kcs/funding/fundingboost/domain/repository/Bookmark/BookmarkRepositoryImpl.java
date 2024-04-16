package kcs.funding.fundingboost.domain.repository.Bookmark;

import static kcs.funding.fundingboost.domain.entity.QBookmark.bookmark;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kcs.funding.fundingboost.domain.entity.Bookmark;
import kcs.funding.fundingboost.domain.entity.QItem;
import kcs.funding.fundingboost.domain.entity.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Bookmark findBookmarkByMemberAndItem(Long memberId, Long itemId) {
        return queryFactory
                .selectFrom(bookmark)
                .join(bookmark.item, QItem.item).fetchJoin()
                .join(bookmark.member, QMember.member).fetchJoin()
                .where(bookmark.member.memberId.eq(memberId)
                        .and(bookmark.item.itemId.eq(itemId)))
                .fetchOne();
    }
}
