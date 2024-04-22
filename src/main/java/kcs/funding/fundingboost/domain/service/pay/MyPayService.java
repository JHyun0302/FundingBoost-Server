package kcs.funding.fundingboost.domain.service.pay;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.OrderItemsDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingPayViewDto;
import kcs.funding.fundingboost.domain.dto.response.MyOrderPayViewDto;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.FundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPayService {

    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final FundingRepository fundingRepository;

    public MyFundingPayViewDto fundingPay(Long fundingItemId, Long memberId) {

        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_FUNDINGITEM));

        List<DeliveryDto> deliveryDtoList = deliveryRepository.findAllByMemberId(memberId)
                .stream()
                .map(DeliveryDto::fromEntity)
                .toList();

        return MyFundingPayViewDto.fromEntity(fundingItem, deliveryDtoList);
    }

    public MyOrderPayViewDto orderPay(OrderItemsDto orderItemsDto, Long memberId) {

        List<DeliveryDto> deliveryDtoList = deliveryRepository.findAllByMemberId(memberId)
                .stream()
                .map(DeliveryDto::fromEntity)
                .toList();
        int point = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER))
                .getPoint();
        return MyOrderPayViewDto.fromEntity(orderItemsDto, deliveryDtoList, point);
    }


    @Transactional
    public CommonSuccessDto pay(PaymentDto paymentDto, Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        if (findMember.getPoint() - paymentDto.usingPoint() >= 0) {
            findMember.minusPoint(paymentDto.usingPoint());
        } else {
            throw new RuntimeException("point가 부족합니다");
        }
        return CommonSuccessDto.fromEntity(true);
    }
}
