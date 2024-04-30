package kcs.funding.fundingboost.domain.service;

import java.util.Comparator;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.myPage.myFundingStatus.TransformPointDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final FundingRepository fundingRepository;

    @Transactional
    public CommonSuccessDto exchangePoint(TransformPointDto transformPointDto) {
        Funding funding = fundingRepository.findMemberByFundingId(transformPointDto.fundingId());
        Member member = funding.getMember();
        List<FundingItem> fundingItems = funding.getFundingItems();

        List<FundingItem> sortedFundingItems = fundingItems.stream()
                .sorted(Comparator.comparingInt(FundingItem::getItemSequence))
                .toList();

        int collectPrice = funding.getCollectPrice();
        for (FundingItem sortedFundingItem : sortedFundingItems) {
            if (collectPrice - sortedFundingItem.getItem().getItemPrice() >= 0) {
                collectPrice -= sortedFundingItem.getItem().getItemPrice();
            } else {
                member.plusPoint(collectPrice);
                sortedFundingItem.finishFunding();
                return CommonSuccessDto.fromEntity(true);
            }
        }
        return CommonSuccessDto.fromEntity(true);
    }
}
