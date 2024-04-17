package kcs.funding.fundingboost.domain.service.pay;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendPayService {

    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;

    public FriendFundingPayingDto getFriendFundingPay(Long fundingId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();

        return FriendFundingPayingDto.fromEntity(friendFunding, member.getPoint());
    }

    public CommonSuccessDto payForFriend(Long memberId, Long fundingId,
        FriendPayProcessDto friendPayProcessDto) {
        processFriendPayment(memberId, fundingId, friendPayProcessDto.myPoint());
        return CommonSuccessDto.fromEntity(true);
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
