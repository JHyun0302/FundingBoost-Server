package kcs.funding.fundingboost.elasticsearch.repository;

import kcs.funding.fundingboost.elasticsearch.index.ItemIndex;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ItemIndexRepositoryCustom {
    Slice<ItemIndex> findByCategoryOrItemName(String keyword, Pageable pageable);
}
