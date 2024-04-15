package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;

    @GetMapping("/api/v1/funding")
    public ResponseDto<List<FundingRegistrationItemDto>> viewFundingRegistration(
            @RequestParam(name = "memberId") Long memberId,
            @RequestBody List<Long> itemList
    ){
        return ResponseDto.ok(fundingService.getFundingRegister(itemList));
    }

    @PostMapping("/api/v1/funding")
    public ResponseDto<CommonSuccessDto> registerFunding(
            @RequestParam(name = "memberId") Long memberId,
            @RequestBody RegisterFundingDto registerFundingDto
    ){

        return ResponseDto.created(fundingService.putFundingAndFundingItem(memberId, registerFundingDto));
    }
}
