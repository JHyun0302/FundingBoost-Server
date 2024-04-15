package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
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
    public MyPayViewDto myOrdePayView( @RequestParam(name = "memberId") Long memberId) {
        return myPayService.viewOrder(memberId);
    }

    @GetMapping("/funding")
    public MyPayViewDto myFundingPayView( @RequestParam(name = "memberId") Long memberId){
        return myPayService.viewFunding(memberId);
    }

    @PostMapping("/payment")
    public ResponseDto<CommonSuccessDto> processPayment( @RequestBody PaymentDto paymentDto, @RequestParam("memberId") Long memberId){
        return ResponseDto.created(paymentService.processPayment(paymentDto, memberId));
    }
}


