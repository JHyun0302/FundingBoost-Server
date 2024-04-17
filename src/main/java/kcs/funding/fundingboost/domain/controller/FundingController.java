package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingDetailDto;
import kcs.funding.fundingboost.domain.dto.response.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funding")
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;

    @GetMapping("")
    public ResponseDto<List<FundingRegistrationItemDto>> viewFundingRegistration(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "ItemList") List<Long> registerFundingBringItemDto
    ){
        return ResponseDto.ok(fundingService.getFundingRegister(registerFundingBringItemDto, memberId));
    }

    @PostMapping("")
    public ResponseDto<CommonSuccessDto> registerFunding(
            @RequestParam(name = "memberId") Long memberId,
            @RequestBody RegisterFundingDto registerFundingDto
    ) {

        return ResponseDto.created(fundingService.putFundingAndFundingItem(memberId, registerFundingDto));
    }

    @Transactional
    @PostMapping("/close/{fundingId}")
    public CommonSuccessDto closeFunding(@PathVariable("fundingId") Long fundingId) {
        return fundingService.terminateFunding(fundingId);
        }
    @GetMapping("/friends/{fundingId}")
    public ResponseDto<FriendFundingDetailDto> viewFreindsFundingDetail(@PathVariable("fundingId") Long fundingId, @RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(fundingService.viewFreindsFundingDetail(fundingId, memberId));
    }
}
