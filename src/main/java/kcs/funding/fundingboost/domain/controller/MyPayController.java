package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.service.MyPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MyPayController {

    private final MyPayService myPayService;

    @GetMapping("/mypay")
    public MyPayViewDto mypayView(Long memberId) {
        return myPayService.func(memberId);
    }
}
