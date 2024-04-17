package kcs.funding.fundingboost.domain.service;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendPayService {

    private final FundingRepository fundingRepository;
    private final MemberRepository memberRepository;

    public FriendFundingPayingDto findFriendFundingPay(Long fundingId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();

        return FriendFundingPayingDto.fromEntity(friendFunding, member.getPoint());
    }

    @Transactional
    public CommonSuccessDto payFund(Long memberId, Long fundingId, FriendPayProcessDto friendPayProcessDto) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        if (member.getPoint() >= friendPayProcessDto.myPoint()) {
            member.minusPoint(friendPayProcessDto.myPoint());
        } else {
            throw new RuntimeException("포인트가 부족합니다");
        }

        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();
        if (friendFunding.getCollectPrice() + friendPayProcessDto.myPoint()
            <= friendFunding.getTotalPrice()) {
            friendFunding.fund(friendPayProcessDto.myPoint());
        } else {
            throw new RuntimeException("설정된 펀딩액 이상을 후원할 수 없습니다");
        }

        return CommonSuccessDto.fromEntity(true);
    }
}
