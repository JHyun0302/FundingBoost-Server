package kcs.funding.fundingboost.elasticsearch.controller;

import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.response.shopping.ShopDto;
import kcs.funding.fundingboost.elasticsearch.service.ItemIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3")
@RequiredArgsConstructor
public class SearchController {

    private final ItemIndexService itemIndexService;

    @GetMapping("/search")
    public ResponseDto<Slice<ShopDto>> searchItem(
            @RequestParam(name = "keyword", required = true) String keyword,
            Pageable pageable
    ) {
        return ResponseDto.ok(itemIndexService.searchWithCategoryAndName(keyword, pageable));
    }

    @GetMapping("/items")
    public ResponseDto<Slice<ShopDto>> searchCategory(
            @RequestParam(name = "category", required = false) String category,
            Pageable pageable
    ) {
        return ResponseDto.ok(itemIndexService.searchWithCategory(category, pageable));
    }
}
