package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.TransformPointDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingStatusDto;
import kcs.funding.fundingboost.domain.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @PostMapping("/point")
    public ResponseDto<CommonSuccessDto> exchangePoint(@RequestBody TransformPointDto transformPointDto,
                                                       @RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPageService.exchangePoint(transformPointDto));
    }

    @GetMapping("")
    public ResponseDto<MyFundingStatusDto> viewMyPage(@RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPageService.getMyFundingStatus(memberId));
    }
}
