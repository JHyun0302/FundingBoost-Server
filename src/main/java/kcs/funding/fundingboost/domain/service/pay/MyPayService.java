package kcs.funding.fundingboost.domain.service.pay;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
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

@Service
@RequiredArgsConstructor
public class MyPayService {

    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    public MyPayViewDto fundingPay(Long memberId) {
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

    public MyPayViewDto orderPay(Long memberId) {
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

    public CommonSuccessDto payForme(PaymentDto paymentDto, Long memberId) {
        processMyPayment(memberId, paymentDto.usingPoint());
        return CommonSuccessDto.fromEntity(true);
    }

    public void processMyPayment(Long memberId, int price) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        calculatePoint(price, findMember);
    }

    private static void calculatePoint(int price, Member findMember) {
        if (findMember.getPoint() - price >= 0) {
            findMember.minusPoint(price);
        } else {
            throw new RuntimeException("Point가 음수가 돼 버렸네용~");
        }
    }
}
