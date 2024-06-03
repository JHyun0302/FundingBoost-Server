package kcs.funding.fundingboost.elasticsearch.service;

import kcs.funding.fundingboost.domain.dto.response.shopping.ShopDto;
import kcs.funding.fundingboost.elasticsearch.index.ItemIndex;
import kcs.funding.fundingboost.elasticsearch.repository.ItemIndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemIndexService {

    private final ItemIndexRepository itemIndexRepository;

    public Slice<ShopDto> searchByKeyword(String keyword, Pageable pageable) {
        Slice<ItemIndex> items = itemIndexRepository.findByCategoryOrItemName(keyword, pageable);
        return items.map(ShopDto::fromIndex);
    }
}
