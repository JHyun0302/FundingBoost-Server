package kcs.funding.fundingboost.domain.repository.item;

import kcs.funding.fundingboost.domain.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

public interface ItemRepositoryCustom {

    Slice<Item> findItemsByCategory(@Param("category") String category, Pageable pageable);

    Slice<Item> findItemsBySlice(Pageable pageable);
}
