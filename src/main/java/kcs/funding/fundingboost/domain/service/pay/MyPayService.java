package kcs.funding.fundingboost.domain.service.pay;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_POINT_LACK;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.FundingPaymentDto;
import kcs.funding.fundingboost.domain.dto.request.MyPayDto;
import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.FundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.OrderRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPayService {
    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final FundingItemRepository fundingItemRepository;

    public MyPayViewDto getMyFundingPay(Long memberId) {
        Funding funding = fundingRepository.findByMemberIdAndStatus(memberId, true);
        List<ItemDto> itemDtoList = funding.getFundingItems()
                .stream()
                .map(ItemDto::fromEntity)
                .toList();

        List<DeliveryDto> deliveryDtoList = deliveryRepository.findAllByMemberId(memberId)
                .stream()
                .map(DeliveryDto::fromEntity)
                .toList();

        return MyPayViewDto.fromEntity(itemDtoList, deliveryDtoList, funding);
    }

    public MyPayViewDto getMyOrderPay(Long memberId) {
        List<Order> orders = orderRepository.findAllByMemberId(memberId);
        List<ItemDto> itemDtoList = orders.stream()
                .map(ItemDto::fromEntity)
                .toList();

        List<Delivery> deliveries = deliveryRepository.findAllByMemberId(memberId);
        List<DeliveryDto> deliveryDtoList = deliveries.stream()
                .map(DeliveryDto::fromEntity)
                .toList();

        return MyPayViewDto.fromEntity(itemDtoList, deliveryDtoList, orders.get(0));
    }

    @Transactional
    public CommonSuccessDto payMyItem(MyPayDto paymentDto, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        deductPointsIfPossible(findMember, paymentDto.usingPoint());
        return CommonSuccessDto.fromEntity(true);
    }

    @Transactional
    public CommonSuccessDto payMyFunding(FundingPaymentDto fundingPaymentDto, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        FundingItem fundingItem = fundingItemRepository.findById(fundingPaymentDto.fundingItemId()).orElseThrow();
        deductPointsIfPossible(findMember, fundingPaymentDto.usingPoint());
        fundingItem.finishFunding();
        return CommonSuccessDto.fromEntity(true);
    }

    private void deductPointsIfPossible(Member member, int points) {
        if (member.getPoint() - points >= 0) {
            member.minusPoint(points);
        } else {
            throw new CommonException(INVALID_POINT_LACK);
        }
    }
}
