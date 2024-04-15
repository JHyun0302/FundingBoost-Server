package kcs.funding.fundingboost.domain.controller;

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
    public MyPayViewDto myOrdePayView( @RequestParam(name = "memberId") Long memberId) {
        return myPayService.viewOrder(memberId);
    }

    @GetMapping("/funding")
    public MyPayViewDto myFundingPayView( @RequestParam(name = "memberId") Long memberId){
        return myPayService.viewFunding(memberId);
    }

}
