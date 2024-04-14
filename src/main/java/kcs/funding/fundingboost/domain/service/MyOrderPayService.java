package kcs.funding.fundingboost.domain.service;

import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.FundingRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyOrderPayService {

    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    public MyPayViewDto func(Long memberId) {
        //point
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("member not found"));
        int point = member.getPoint();

        //ItemListDto
        List<Order> orders = orderRepository.findAllByMember(member);
        List<ItemDto> ItemListDto = orders.stream().map(o -> ItemDto.fromEntity(o)).toList();

        //DeliveryListDto
        List<Delivery> deliveries = deliveryRepository.findAllByMember(member);
        List<DeliveryDto> deliveryListDto = deliveries.stream().map(d -> DeliveryDto.fromEntity(d)).toList();



       return MyPayViewDto.fromEntity(ItemListDto,deliveryListDto,point);

    }
}
