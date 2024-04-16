package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.service.MyPayService;
import kcs.funding.fundingboost.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypay")
public class MyPayController {

    private final MyPayService myPayService;
    private final PaymentService paymentService;

    @GetMapping("/order")
    public ResponseDto<MyPayViewDto> viewMyOrderPay(@RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(myPayService.viewOrder(memberId));
    }

    @GetMapping("/funding")
    public ResponseDto<MyPayViewDto> viewMyFundingPay( @RequestParam(name = "memberId") Long memberId){
        return ResponseDto.ok(myPayService.viewFunding(memberId));
    }

    @PostMapping("/payment")
    public ResponseDto<CommonSuccessDto> processMyPayment( @RequestBody PaymentDto paymentDto, @RequestParam("memberId") Long memberId){
        return ResponseDto.ok(paymentService.processMyPayment(paymentDto, memberId));
    }
}
