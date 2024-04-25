package kcs.funding.fundingboost.domain.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.TransformPointDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingResponseDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.MyPageFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.ParticipateFriendDto;
import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
import kcs.funding.fundingboost.domain.repository.Contributor.ContributorRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final FundingRepository fundingRepository;
    private final MemberRepository memberRepository;
    private final ContributorRepository contributorRepository;

    @Transactional
    public CommonSuccessDto exchangePoint(TransformPointDto transformPointDto) {
        Funding funding = fundingRepository.findMemberByFundingId(transformPointDto.fundingId());
        Member member = funding.getMember();
        List<FundingItem> fundingItems = funding.getFundingItems();

        List<FundingItem> sortedFundingItems = fundingItems.stream()
                .sorted(Comparator.comparingInt(FundingItem::getItemSequence))
                .collect(Collectors.toList());

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

    public MyFundingStatusDto getMyFundingStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        Funding funding = fundingRepository.findByMemberIdAndStatus(member.getMemberId(), true);
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);
        if (funding == null) { //TODO: optional
            return MyFundingStatusDto.createNotExistFundingMyFundingStatusDto(myPageMemberDto);
        }
        List<MyPageFundingItemDto> myPageFundingItemList = getMyPageFundingItemDtoList(funding);
        List<ParticipateFriendDto> participateFriendDtoList = getParticipateFriendDtoList(funding);

        int totalPercent = funding.getCollectPrice() * 100 / funding.getTotalPrice();
        int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(),
                funding.getDeadline());
        String deadlineDate = "D-" + leftDate;

        return MyFundingStatusDto.createMyFundingStatusDto(
                myPageMemberDto,
                myPageFundingItemList,
                participateFriendDtoList,
                totalPercent,
                funding.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                deadlineDate
        );
    }

    private List<ParticipateFriendDto> getParticipateFriendDtoList(Funding funding) {
        List<Contributor> contributorList = contributorRepository.findAllByFundingId(funding.getFundingId());
        return contributorList.stream()
                .map(ParticipateFriendDto::fromEntity).toList();
    }

    private List<MyPageFundingItemDto> getMyPageFundingItemDtoList(Funding funding) {
        List<FundingItem> fundingItemList = funding.getFundingItems();
        List<MyPageFundingItemDto> myPageFundingItemList = new ArrayList<>();
        int collectPrice = funding.getCollectPrice();

        for (FundingItem fundingItem : fundingItemList) {
            int itemPercent = 0;
            if (collectPrice >= fundingItem.getItem().getItemPrice()) {
                collectPrice -= fundingItem.getItem().getItemPrice();
                itemPercent = 100;
            } else {
                itemPercent = collectPrice * 100 / fundingItem.getItem().getItemPrice();
            }
            myPageFundingItemList.add(MyPageFundingItemDto.fromEntity(funding, fundingItem.getItem(), itemPercent,
                    fundingItem.isFinishedStatus()));
        }

        return myPageFundingItemList;
    }

    public MyFundingHistoryDto getMyFundingHistory(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));

        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);

        List<Funding> fundings = fundingRepository.findFundingByMemberId(memberId);

        List<MyFundingResponseDto> myFundingResponseDtos = fundings.stream()
                .map(funding -> {
                    Long contributors = contributorRepository.countContributorsForFunding(funding.getFundingId());
                    return MyFundingResponseDto.fromEntity(funding, contributors);
                })
                .toList();

        return MyFundingHistoryDto.fromEntity(myPageMemberDto, myFundingResponseDtos);
    }
}
