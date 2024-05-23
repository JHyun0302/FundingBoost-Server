package kcs.funding.fundingboost.domain.repository.item;

import static kcs.funding.fundingboost.domain.entity.QItem.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Item> findItemsByCategory(String category, Pageable pageable) {
        List<Item> items = queryFactory
                .selectFrom(item)
                .where(isCategory(category))
                .orderBy(item.itemId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = items.size() > pageable.getPageSize();

        if (hasNext) {
            items.remove(items.size() - 1);
        }

        return new SliceImpl<>(items, pageable, hasNext);
    }

    @Override
    public Slice<Item> findItemsBySlice(Pageable pageable) {
        List<Item> items = queryFactory
                .selectFrom(item)
                .orderBy(item.itemId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = items.size() > pageable.getPageSize();

        if (hasNext) {
            items.remove(items.size() - 1);
        }

        return new SliceImpl<>(items, pageable, hasNext);
    }

    private BooleanExpression isCategory(String category) {
        if (category == null) {
            return null;
        }
        return item.category.eq(category);
    }
}
