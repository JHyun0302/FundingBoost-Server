package kcs.funding.fundingboost.domain.service;

import java.util.List;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.response.ShopDto;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<ShopDto> getItems() {
        List<Item> items = itemRepository.findAll();

        return items.stream()
                .map(ShopDto::createGiftHubDto)
                .collect(Collectors.toList());
    }
}
