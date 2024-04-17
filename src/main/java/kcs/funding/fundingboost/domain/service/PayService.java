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
import kcs.funding.fundingboost.domain.repository.FundingItem.FundingItemRepository;
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
    private final FundingItemRepository fundingItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final MemberRepository memberRepository;

    public MyPayViewDto viewFunding(Long memberId){
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

        return MyPayViewDto.fromEntity(itemDtoList,deliveryDtoList,point);
    }

    public CommonSuccessDto pay(PaymentDto paymentDto, Long memberId) {
        Delivery delivery = deliveryRepository
            .findById(paymentDto.deliveryId())
            .orElseThrow(()->new RuntimeException("Delivery not found"));
        delivery.successDelivery();
        paymentService.processMyPayment(memberId, paymentDto.usingPoint());

        return CommonSuccessDto.fromEntity(true);
    }

    public FriendFundingPayingDto findFriendFundingPay(Long fundingId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();

        return FriendFundingPayingDto.fromEntity(friendFunding, member.getPoint());
    }

    @Transactional
    public CommonSuccessDto pay(Long memberId, Long fundingId, FriendPayProcessDto friendPayProcessDto) {
        paymentService.processFriendPayment(memberId, fundingId, friendPayProcessDto.myPoint());
        return CommonSuccessDto.fromEntity(true);
    }
}
