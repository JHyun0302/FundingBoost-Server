package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;

    @GetMapping("/api/v1/funding")
    public ResponseDto<List<FundingRegistrationItemDto>> viewFundingRegistration(
            Long memberId,
            @RequestBody List<Long> itemList
    ){
        return ResponseDto.ok(fundingService.getFundingRegister(itemList));
    }

    @PostMapping("/api/v1/funding")
    public ResponseDto<CommonSuccessDto> registerFunding(
            Long memberId,
            @RequestBody RegisterFundingDto registerFundingDto
    ){

        return ResponseDto.created(fundingService.putFundingAndFundingItem(memberId, registerFundingDto));
    }
}
