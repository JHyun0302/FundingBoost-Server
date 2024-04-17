package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.service.FriendPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends/pay")
public class FriendPayController {

    private final FriendPayService friendPayService;

    @GetMapping("/{fundingId}")
    public ResponseDto<FriendFundingPayingDto> friendPayView(
        @RequestParam("memberId") Long memberId,
        @PathVariable("fundingId") Long fundingId) {
        return ResponseDto.ok(friendPayService.findFriendFundingPay(fundingId, memberId));
    }

    @PostMapping("/{fundingId}")
    public ResponseDto<CommonSuccessDto> pay(
        @RequestParam("memberId") Long memberId,
        @PathVariable("fundingId") Long fundingId,
        @RequestBody FriendPayProcessDto friendPayProcessDto) {
        return ResponseDto.ok(
            friendPayService.pay(memberId, fundingId, friendPayProcessDto));
    }
}
