package kcs.funding.fundingboost.domain.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.TransformPointDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.FundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;

    private final FundingRepository fundingRepository;

    private final FundingItemRepository fundingItemRepository;

    @Transactional
    public CommonSuccessDto exchangePoint(TransformPointDto transformPointDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        Funding funding = fundingRepository.findById(transformPointDto.fundingId()).orElseThrow();
        List<FundingItem> fundingItems = fundingItemRepository.findFundingItemsByFundingId(
                transformPointDto.fundingId());

        List<FundingItem> sortedFundingItems = fundingItems.stream()
                .sorted(Comparator.comparingInt(FundingItem::getItemSequence))
                .collect(Collectors.toList());

        int collectPrice = funding.getCollectPrice();
        for (FundingItem sortedFundingItem : sortedFundingItems) {
            if (collectPrice - sortedFundingItem.getItem().getItemPrice() >= 0) {
                collectPrice -= sortedFundingItem.getItem().getItemPrice();
            } else {
                member.plusPoint(collectPrice);
                return CommonSuccessDto.fromEntity(true);
            }
        }
        return CommonSuccessDto.fromEntity(true);
    }
}
