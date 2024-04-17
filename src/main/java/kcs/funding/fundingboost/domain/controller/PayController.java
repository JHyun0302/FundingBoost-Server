package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.service.pay.FriendPayService;
import kcs.funding.fundingboost.domain.service.pay.MyPayService;
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
@RequestMapping("/api/v1/pay")
public class PayController {

    private final MyPayService myPayService;
    private final FriendPayService friendPayService;

    @GetMapping("/order")
    public ResponseDto<MyPayViewDto> myOrderPayView(@RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(myPayService.orderPay(memberId));
    }

    @GetMapping("/funding")
    public ResponseDto<MyPayViewDto> myFundingPayView( @RequestParam(name = "memberId") Long memberId){
        return ResponseDto.ok(myPayService.fundingPay(memberId));
    }

    @PostMapping
    public ResponseDto<CommonSuccessDto> payMyOrder( @RequestBody PaymentDto paymentDto, @RequestParam("memberId") Long memberId){
        return ResponseDto.ok(myPayService.pay(paymentDto, memberId));
    }

    @GetMapping("/friends/{fundingId}")
    public ResponseDto<FriendFundingPayingDto> friendPayView(
        @RequestParam("memberId") Long memberId,
        @PathVariable("fundingId") Long fundingId) {
        return ResponseDto.ok(friendPayService.getFriendFundingPay(fundingId, memberId));
    }

    @PostMapping("/friends/{fundingId}")
    public ResponseDto<CommonSuccessDto> fundFriend(
        @RequestParam("memberId") Long memberId,
        @PathVariable("fundingId") Long fundingId,
        @RequestBody FriendPayProcessDto friendPayProcessDto) {
        return ResponseDto.ok(friendPayService.fund(memberId, fundingId, friendPayProcessDto));
    }
}
