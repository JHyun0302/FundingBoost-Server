package kcs.funding.fundingboost.elasticsearch.repository;

import java.util.List;
import kcs.funding.fundingboost.elasticsearch.index.ItemIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemIndexRepositoryImpl implements ItemIndexRepositoryCustom {
    private final ElasticsearchOperations elasticSearchOperations;

    @Override
    public Slice<ItemIndex> findByCategoryOrItemName(String keyword, Pageable pageable) {
        Criteria criteria = new Criteria("category").is(keyword)
                .or("item_name").contains(keyword);
        CriteriaQuery query = new CriteriaQuery(criteria)
                .addSort(defaultSort())
                .setPageable(overFetchPageable(pageable));

        return searchAsSlice(query, pageable);
    }

    @Override
    public Slice<ItemIndex> findByCategory(String keyword, Pageable pageable) {
        Criteria criteria = new Criteria("category").is(keyword);
        CriteriaQuery query = new CriteriaQuery(criteria)
                .addSort(defaultSort())
                .setPageable(overFetchPageable(pageable));
        return searchAsSlice(query, pageable);
    }

    private Slice<ItemIndex> searchAsSlice(CriteriaQuery query, Pageable pageable) {
        SearchHits<ItemIndex> searchHits = elasticSearchOperations.search(query, ItemIndex.class);
        List<ItemIndex> items = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        boolean hasNext = items.size() > pageable.getPageSize();
        if (hasNext) {
            items = items.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(items, pageable, hasNext);
    }

    private Pageable overFetchPageable(Pageable pageable) {
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : defaultSort();
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() + 1, sort);
    }

    private Sort defaultSort() {
        return Sort.by(Sort.Order.desc("itemId"));
    }
}
