package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.commonSuccessDto;
import kcs.funding.fundingboost.domain.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FundingController {

    private final FundingService fundingService;

    @PostMapping("/funding/close/{fundingId}")
    public commonSuccessDto closeFunding(@PathVariable("fundingId") Long fundingId) {
        return fundingService.terminateFunding(fundingId);
    }
}
