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
        member.minusPoint(friendPayProcessDto.myPoint());

        Funding friendFunding = fundingRepository.findById(fundingId).orElseThrow();
        friendFunding.fund(friendPayProcessDto.myPoint());

        return CommonSuccessDto.fromEntity(true);
    }
}
