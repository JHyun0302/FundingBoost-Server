package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.TransformPointDto;
import kcs.funding.fundingboost.domain.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @PostMapping("/point")
    public ResponseDto<CommonSuccessDto> exchangePoint(@RequestBody TransformPointDto transformPointDto,
                                                       @RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPageService.exchangePoint(transformPointDto, memberId));
    }
}
