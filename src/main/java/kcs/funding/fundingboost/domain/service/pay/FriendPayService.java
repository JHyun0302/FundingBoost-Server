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

    public CommonSuccessDto fund(Long memberId, Long fundingId,
        FriendPayProcessDto friendPayProcessDto) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        int point = friendPayProcessDto.myPoint();
        if (findMember.getPoint() - point >= 0) {
            findMember.minusPoint(point);
        } else {
            throw new RuntimeException("point가 부족합니다");
        }
        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();
        if (friendFunding.getCollectPrice() + point <= friendFunding.getTotalPrice()) {
            friendFunding.fund(point);
        } else {
            throw new RuntimeException("설정된 펀딩액 이상을 후원할 수 없습니다");
        }        return CommonSuccessDto.fromEntity(true);
    }
}
