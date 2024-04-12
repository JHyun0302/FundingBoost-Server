package kcs.funding.fundingboost.domain.service;

import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPayViewDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.FundingRepository;
import kcs.funding.fundingboost.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPayService {

    private final FundingRepository fundingRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;



    public MyPayViewDto func(Long memberId) {
        Funding funding = fundingRepository.findById(memberId).orElseThrow(() -> new RuntimeException());
        int point = funding.getMember().getPoint();
        int total = funding.getTotalPrice();


        List<Delivery> deliveries = deliveryRepository.findAll();
        List<DeliveryDto> deliveryListDto = deliveries.stream().map(d -> DeliveryDto.fromEntity(d)).toList();

        List<Order> orders = orderRepository.findAll();
        List<ItemDto> ItemListDto = orders.stream().map(o -> ItemDto.fromEntity(o)).toList();

        return MyPayViewDto.fromEntity(ItemListDto,deliveryListDto,point,total);

    }
}
