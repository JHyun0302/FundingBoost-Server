package kcs.funding.fundingboost.domain.controller;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.request.MyPayDto;
import kcs.funding.fundingboost.domain.dto.request.PayRemainDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
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
    @GetMapping("/order")
    public ResponseDto<MyOrderPayViewDto> myOrderPayView(
            @RequestParam(name = "itemId") List<Long> itemIds,
            @RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(myPayService.myOrderPayView(itemIds, memberId));
    }

    @GetMapping("/order/now")
    public ResponseDto<MyNowOrderPayViewDto> MyOrderNowPayView(
            @RequestParam(name = "itemId") Long itemDto,
            @RequestParam(name = "memberId") Long memberId) {
        return ResponseDto.ok(myPayService.MyOrderNowPayView(itemDto, memberId));
    }

    /**
     * 마이 페이 펀딩 페이지 조회
     */
    @GetMapping("/funding/{fundingItemId}")
    public ResponseDto<MyFundingPayViewDto> myFundingPayView(
            @PathVariable(name = "fundingItemId") Long fundingItemId,
            @RequestParam(name = "memberId") Long memberId) {

        return ResponseDto.ok(myPayService.myFundingPayView(fundingItemId, memberId));
    }

    /**
     * 상품 구매하기
     */
    @PostMapping("/order")
    public ResponseDto<CommonSuccessDto> payMyOrder(@RequestBody MyPayDto paymentDto,
                                                    @RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPayService.payMyItem(paymentDto, memberId));
    }

    /**
     * 펀딩 상품 구매하기
     */
    @PostMapping("/funding/{fundingItemId}")
    public ResponseDto<CommonSuccessDto> payMyFunding(@PathVariable("fundingItemId") Long fundingItemId,
                                                      @RequestBody PayRemainDto payRemainDto,
                                                      @RequestParam("memeberId") Long memberId) {
        return ResponseDto.ok(myPayService.payMyFunding(fundingItemId, payRemainDto, memberId));
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
