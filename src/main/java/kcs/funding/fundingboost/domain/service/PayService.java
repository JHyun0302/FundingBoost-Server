package kcs.funding.fundingboost.domain.service;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.OrderRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayService {

    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    public MyPayViewDto viewFunding(Long memberId) {
        Funding funding = fundingRepository.findByMemberIdAndStatus(memberId, true);
        List<ItemDto> itemDtoList = funding.getFundingItems()
            .stream()
            .map(f -> ItemDto.fromEntity(f))
            .toList();

        List<DeliveryDto> deliveryDtoList = deliveryRepository.findAllByMemberId(memberId)
            .stream()
            .map(d -> DeliveryDto.fromEntity(d))
            .toList();
        int point = funding.getMember().getPoint();
        int collectPrice = funding.getCollectPrice();

        return MyPayViewDto.fromEntity(itemDtoList, deliveryDtoList, point, collectPrice);
    }

    public MyPayViewDto viewOrder(Long memberId) {

        List<Order> orders = orderRepository.findAllByMemberId(memberId);
        List<ItemDto> itemDtoList = orders.stream()
            .map(o -> ItemDto.fromEntity(o))
            .toList();

        List<Delivery> deliveries = deliveryRepository.findAllByMemberId(memberId);
        List<DeliveryDto> deliveryDtoList = deliveries.stream()
            .map(d -> DeliveryDto.fromEntity(d))
            .toList();

        int point = orders.get(0).getMember().getPoint();

        return MyPayViewDto.fromEntity(itemDtoList, deliveryDtoList, point);
    }

    public CommonSuccessDto pay(PaymentDto paymentDto, Long memberId) {
        processMyPayment(memberId, paymentDto.usingPoint());

        return CommonSuccessDto.fromEntity(true);
    }

    public FriendFundingPayingDto findFriendFundingPay(Long fundingId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();

        return FriendFundingPayingDto.fromEntity(friendFunding, member.getPoint());
    }

    @Transactional
    public CommonSuccessDto pay(Long memberId, Long fundingId,
        FriendPayProcessDto friendPayProcessDto) {
        processFriendPayment(memberId, fundingId, friendPayProcessDto.myPoint());
        return CommonSuccessDto.fromEntity(true);
    }

    public void processMyPayment(Long memberId, int price) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        calculatePoint(price, findMember);
    }

    public void processFriendPayment(Long memberId, Long fundingId, int point) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        calculatePoint(point, findMember);

        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();
        if (friendFunding.getCollectPrice() + point <= friendFunding.getTotalPrice()) {
            friendFunding.fund(point);
        } else {
            throw new RuntimeException("설정된 펀딩액 이상을 후원할 수 없습니다");
        }
    }

    private static void calculatePoint(int price, Member findMember) {
        if (findMember.getPoint() - price >= 0) {
            findMember.minusPoint(price);
        } else {
            throw new RuntimeException("Point가 음수가 돼 버렸네용~");
        }
    }
}
