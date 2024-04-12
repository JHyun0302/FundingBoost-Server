package kcs.funding.fundingboost.domain.controller;


import java.util.List;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.response.GiftHubDto;
import kcs.funding.fundingboost.domain.service.GiftHubItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class GiftHubItemController {

    private final GiftHubItemService giftHubItemService;

    @GetMapping("/gifthub")
    public ResponseDto<List<GiftHubDto>> GiftHubItemController(@RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(giftHubItemService.getGiftHub(memberId));
    }

}
