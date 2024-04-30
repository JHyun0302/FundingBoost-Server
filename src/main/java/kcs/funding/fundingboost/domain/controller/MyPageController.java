package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.TransformPointDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingHistoryDetailDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.MyPageDeliveryManageDto;
import kcs.funding.fundingboost.domain.service.MyPageService;
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
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 포인트 전환하기
     */
    @PostMapping("/point")
    public ResponseDto<CommonSuccessDto> exchangePoint(@RequestBody TransformPointDto transformPointDto,
                                                       @RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPageService.exchangePoint(transformPointDto));
    }

    @GetMapping("")
    public ResponseDto<MyFundingStatusDto> viewMyPage(@RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPageService.getMyFundingStatus(memberId));
    }

    /**
     * 지난 펀딩 이력 조회
     */
    @GetMapping("/funding-history")
    public ResponseDto<MyFundingHistoryDto> viewMyFundingHistory(@RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPageService.getMyFundingHistory(memberId));
    }

    /**
     * 지난 펀딩 이력 상세 조회
     */
    @GetMapping("/funding-history/{fundingId}")
    public ResponseDto<MyFundingHistoryDetailDto> viewMyFundingHistoryDetail(@RequestParam("memberId") Long memberId,
                                                                             @PathVariable("fundingId") Long fundingId) {
        return ResponseDto.ok(myPageService.getMyFundingHistoryDetails(memberId, fundingId));
    }

    /**
     * 배송지 관리 조회
     */
    @GetMapping("/delivery")
    public ResponseDto<MyPageDeliveryManageDto> viewMyDeliveryManagement(@RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(myPageService.getMyDeliveryManageList(memberId));
    }
}
