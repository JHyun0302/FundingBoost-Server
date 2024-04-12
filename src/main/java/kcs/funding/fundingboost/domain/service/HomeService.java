package kcs.funding.fundingboost.domain.service;

import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.response.ViewMainDto;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.FundingRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final RelationshipRepository relationshipRepository;


    public ViewMainDto getMainView(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        List<Member> friends = relationshipRepository.findAllByMemberId(memberId);

    }
}
