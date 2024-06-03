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
@RequestMapping("/api/v3/search")
@RequiredArgsConstructor
public class SearchController {

    private final ItemIndexService itemIndexService;

    @GetMapping("")
    public ResponseDto<Slice<ShopDto>> searchItem(
            @RequestParam(name = "keyword", required = true) String keyword,
            Pageable pageable
    ) {
        return ResponseDto.ok(itemIndexService.searchByKeyword(keyword, pageable));
    }
}
