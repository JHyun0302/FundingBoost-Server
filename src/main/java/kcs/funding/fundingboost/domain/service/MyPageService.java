package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_FUNDING_STATUS;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_CONTRIBUTOR;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_FUNDING;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.TransformPointDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingHistoryDetailDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingResponseDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.MyPageFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.ParticipateFriendDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory.FriendFundingContributionDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory.FriendFundingHistoryDto;
import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.contributor.ContributorRepository;
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
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
        Optional<Funding> funding = fundingRepository.findByMemberIdAndStatus(member.getMemberId(), true);
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);
        if (funding.isPresent()) {
            return MyFundingStatusDto.createNotExistFundingMyFundingStatusDto(myPageMemberDto);
        }
        List<MyPageFundingItemDto> myPageFundingItemList = getMyPageFundingItemDtoList(funding.get());
        List<ParticipateFriendDto> participateFriendDtoList = getParticipateFriendDtoList(funding.get());

        int totalPercent = funding.get().getCollectPrice() * 100 / funding.get().getTotalPrice();
        int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(),
                funding.get().getDeadline());
        String deadlineDate = "D-" + leftDate;

        return MyFundingStatusDto.createMyFundingStatusDto(
                myPageMemberDto,
                myPageFundingItemList,
                participateFriendDtoList,
                totalPercent,
                funding.get().getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
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
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));

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

    public MyFundingHistoryDetailDto getMyFundingHistoryDetails(Long memberId, Long fundingId) {
        Funding funding = fundingRepository.findByFundingId(fundingId);
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(funding.getMember());
        if (funding.isFundingStatus()) {
            // 펀딩이 진행중인 상황
            throw new CommonException(INVALID_FUNDING_STATUS);
        }
        List<MyPageFundingItemDto> myPageFundingItemDtoList = getMyPageFundingItemDtoList(funding);
        List<ParticipateFriendDto> participateFriendDtoList = getParticipateFriendDtoList(funding);
        int totalPercent = funding.getCollectPrice() * 100 / funding.getTotalPrice();
        return MyFundingHistoryDetailDto.createMyFundingHistoryDetailDto(
                myPageMemberDto,
                myPageFundingItemDtoList,
                participateFriendDtoList,
                totalPercent,
                funding.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                funding.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public FriendFundingHistoryDto getFreindFundingHistory(Long memberId) {

        List<FriendFundingContributionDto> friendFundingContributionDtoList =
                contributorRepository.findAllByMemberId(memberId).stream()
                        .map(contributor -> {
                            Funding contributeFunding = Optional.ofNullable(contributor.getFunding())
                                    .orElseThrow(() -> new CommonException(NOT_FOUND_FUNDING));
                            return FriendFundingContributionDto.fromEntity(contributor, contributeFunding);
                        })
                        .toList();
        if (friendFundingContributionDtoList.isEmpty()) {
            throw new CommonException(NOT_FOUND_CONTRIBUTOR);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);

        return FriendFundingHistoryDto.fromEntity(myPageMemberDto, friendFundingContributionDtoList);
    }
}
