package kcs.funding.fundingboost.elasticsearch.repository;

import java.util.List;
import kcs.funding.fundingboost.elasticsearch.index.ItemIndex;
import lombok.RequiredArgsConstructor;
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
        Criteria criteria = new Criteria("category").contains(keyword)
                .or("item_name").contains(keyword);
        CriteriaQuery query = new CriteriaQuery(criteria)
                .addSort(Sort.by(Sort.Order.asc("item_id")))
                .setPageable(pageable);

        SearchHits<ItemIndex> searchHits = elasticSearchOperations.search(query, ItemIndex.class);
        List<ItemIndex> items = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        boolean hasNext = items.size() == pageable.getPageSize();

        return new SliceImpl<>(items, pageable, hasNext);
    }
}
