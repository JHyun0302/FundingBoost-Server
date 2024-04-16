package kcs.funding.fundingboost.domain.service;

import jakarta.transaction.Transactional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.PaymentDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;
    @Transactional
    public CommonSuccessDto processMyPayment(PaymentDto paymentDto, Long memberId) {

        Delivery delivery = deliveryRepository
                .findById(paymentDto.deliveryId())
                .orElseThrow(()->new RuntimeException("Delivery not found"));
        delivery.successDelivery();


        Member findMember = memberRepository.findById(memberId).orElseThrow();
        if (findMember.getPoint() - paymentDto.usingPoint() >= 0) {
            findMember.minusPoint(paymentDto.usingPoint());
        } else{
            throw new RuntimeException("Point가 음수가 돼 버렸네용~");
        }

        return CommonSuccessDto.fromEntity(true);

    }


}
