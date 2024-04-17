package kcs.funding.fundingboost.domain.service;

import jakarta.transaction.Transactional;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    
    @Transactional
    public void processMyPayment(Long memberId, int price) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        calculatePoint(price, findMember);
    }

    @Transactional
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
        } else{
            throw new RuntimeException("Point가 음수가 돼 버렸네용~");
        }
    }
}
