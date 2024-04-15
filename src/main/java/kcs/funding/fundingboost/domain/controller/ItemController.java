package kcs.funding.fundingboost.domain.controller;


import java.util.List;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.response.ShopDto;
import kcs.funding.fundingboost.domain.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class ItemController {
    
    private final ItemService itemService;

    @GetMapping("/shop")
    public ResponseDto<List<ShopDto>> ShopController() {
        return ResponseDto.ok(itemService.getItems());
    }
}
