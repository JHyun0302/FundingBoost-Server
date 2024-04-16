package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.service.MyPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypay")
public class MyPayController {

    private final MyPayService myPayService;
    @GetMapping("/order")
    public ResponseDto<MyPayViewDto> myOrderPayView(@RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(myPayService.viewOrder(memberId));
    }

    @GetMapping("/funding")
    public ResponseDto<MyPayViewDto> myFundingPayView( @RequestParam(name = "memberId") Long memberId){
        return ResponseDto.ok(myPayService.viewFunding(memberId));
    }

}
