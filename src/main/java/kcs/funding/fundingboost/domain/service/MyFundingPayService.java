package kcs.funding.fundingboost.domain.service;

import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.entity.*;
import kcs.funding.fundingboost.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyFundingPayService {

    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;
    private  final FundingItemRepository fundingItemRepository;
    private final OrderRepository orderRepository;

    public MyPayViewDto func(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("member not found"));
        Funding funding = fundingRepository.findByMemberAndFundingStatus(member, true);

        //ItemListDto
        List<FundingItem> fundingItem = fundingItemRepository.findAllByFunding(funding);
        List<ItemDto> ItemListDto = fundingItem.stream().map(f -> ItemDto.fromEntity(f)).toList();

        //DeliveryListDto
        List<Delivery> deliveries = deliveryRepository.findAllByMember(member);
        List<DeliveryDto> deliveryListDto = deliveries.stream().map(d -> DeliveryDto.fromEntity(d)).toList();

        //point
       int point = funding.getMember().getPoint();

       //CollectPrice
       int collecetPrice = funding.getCollectPrice();


       return MyPayViewDto.fromEntity(ItemListDto,deliveryListDto,point,collecetPrice);

    }
}
