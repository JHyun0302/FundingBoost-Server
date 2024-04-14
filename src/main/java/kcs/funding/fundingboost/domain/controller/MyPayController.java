package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.service.MyFundingPayService;
import kcs.funding.fundingboost.domain.service.MyOrderPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MyPayController {

    private final MyOrderPayService myOrderPayService;
    private final MyFundingPayService myFundingPayService;
    @GetMapping("/mypay/{orderOrFunding}")
    public MyPayViewDto mypayView(@PathVariable String orderOrFunding ,@RequestParam(name = "memberId") Long memberId) {
        if ("order".equals(orderOrFunding)) {
            return myOrderPayService.func(memberId);
        } else if ("funding".equals(orderOrFunding)) {
            return myFundingPayService.func(memberId);
        } else {
            throw new IllegalArgumentException("Invalid value for orderOrFunding parameter");
        }
    }
}
