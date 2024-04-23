package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.request.OrderItemsDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingPayViewDto;
import kcs.funding.fundingboost.domain.dto.response.MyNowOrderPayViewDto;
import kcs.funding.fundingboost.domain.dto.response.MyOrderPayViewDto;
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

    /**
     * 마이 페이 주문 페이지 조회
     */
    @PostMapping("/order")
    public ResponseDto<MyOrderPayViewDto> myOrderPayView(
            @RequestBody OrderItemsDto orderItemDto,
            @RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(myPayService.orderPay(orderItemDto, memberId));
    }

    @PostMapping("/order/now")
    public ResponseDto<MyNowOrderPayViewDto> myOrderNowPayView(
            @RequestBody ItemDto itemDto,
            @RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(myPayService.myOrderNowPayView(itemDto, memberId));
    }

    /**
     * 마이 페이 펀딩 페이지 조회
     */
    @PostMapping("/funding/{fundingItemId}")
    public ResponseDto<MyFundingPayViewDto> myFundingPayView(
            @PathVariable(name = "fundingItemId") Long fundingItemId,
            @RequestParam(name = "memberId") Long memberId) {

        return ResponseDto.ok(myPayService.fundingPay(fundingItemId, memberId));
    }

    /**
     * 결제하기
     */
    @PostMapping("")
    public ResponseDto<CommonSuccessDto> payMyOrder(@RequestBody PaymentDto paymentDto,
                                                    @RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPayService.pay(paymentDto, memberId));
    }

    /**
     * 친구 펀딩 결제 페이지 조회
     */
    @GetMapping("/friends/{fundingId}")
    public ResponseDto<FriendFundingPayingDto> friendPayView(
            @RequestParam("memberId") Long memberId,
            @PathVariable("fundingId") Long fundingId) {
        return ResponseDto.ok(friendPayService.getFriendFundingPay(fundingId, memberId));
    }

    /**
     * 친구 펀딩 결제하기
     */
    @PostMapping("/friends/{fundingId}")
    public ResponseDto<CommonSuccessDto> fundFriend(
            @RequestParam("memberId") Long memberId,
            @PathVariable("fundingId") Long fundingId,
            @RequestBody FriendPayProcessDto friendPayProcessDto) {
        return ResponseDto.ok(friendPayService.fund(memberId, fundingId, friendPayProcessDto));
    }
}
