package kcs.funding.fundingboost.domain.service;

import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.OrderRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPayService {

    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

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
}
