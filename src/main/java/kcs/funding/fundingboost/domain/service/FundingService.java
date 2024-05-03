package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_FUNDING_STATUS;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_FUNDING;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_ITEM;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.fundingRegist.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.common.CommonFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.common.FriendFundingPageItemDto;
import kcs.funding.fundingboost.domain.dto.response.friendFunding.FriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.friendFundingDetail.ContributorDto;
import kcs.funding.fundingboost.domain.dto.response.friendFundingDetail.FriendFundingDetailDto;
import kcs.funding.fundingboost.domain.dto.response.friendFundingDetail.FriendFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeItemDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMemberInfoDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMyFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeViewDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory.FriendFundingContributionDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory.FriendFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingHistory.MyFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingHistory.MyPageFundingDetailHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.MyFundingHistoryDetailDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.MyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.MyPageFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.ParticipateFriendDto;
import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.contributor.ContributorRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.repository.fundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import kcs.funding.fundingboost.domain.service.utils.FundingConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FundingService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final FundingItemRepository fundingItemRepository;
    private final ContributorRepository contributorRepository;
    private final RelationshipRepository relationshipRepository;

//    public List<FundingRegistrationItemDto> getFundingRegister(List<Long> registerFundingBringItemDto, Long memberId) {
//
//        Optional<Funding> funding = fundingRepository.findByMemberIdAndStatus(memberId, true);
//
//        if (funding.isPresent()) {
//            throw new CommonException(ALREADY_EXIST_FUNDING);
//        }
//
//        return IntStream.range(0, registerFundingBringItemDto.size())
//                .mapToObj(i -> FundingRegistrationItemDto.createFundingRegistrationItemDto(
//                        itemRepository.findById(registerFundingBringItemDto.get(i))
//                                .orElseThrow(() -> new CommonException(NOT_FOUND_ITEM)),
//                        (long) i + 1)).toList();
//    }

    @Transactional
    public CommonSuccessDto putFundingAndFundingItem(Long memberId, RegisterFundingDto registerFundingDto) {

        List<Long> registerFundingItemList = registerFundingDto.itemIdList();

        List<Item> itemList = registerFundingItemList.stream()
                .map(itemIdList -> itemRepository.findById(itemIdList)
                        .orElseThrow(() -> new CommonException(NOT_FOUND_ITEM))).toList();

        int sum = 0;
        for (Item item : itemList) {
            sum += item.getItemPrice();
        }

        Funding funding = Funding.createFunding(memberRepository.findById(memberId)
                        .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER)),
                registerFundingDto.fundingMessage(),
                Tag.getTag(registerFundingDto.tag()),
                sum,
                registerFundingDto.deadline().atTime(23, 59, 59));

        fundingRepository.save(funding);

        for (int i = 0; i < registerFundingItemList.size(); i++) {
            FundingItem fundingItem = FundingItem.createFundingItem(
                    funding,
                    itemRepository.findById(registerFundingItemList.get(i))
                            .orElseThrow(() -> new CommonException(NOT_FOUND_ITEM)),
                    i + 1);
            fundingItemRepository.save(fundingItem);
        }

        return CommonSuccessDto.fromEntity(true);
    }

    @Transactional
    public CommonSuccessDto terminateFunding(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_FUNDING));
        funding.terminate();
        return CommonSuccessDto.fromEntity(true);
    }

    public FriendFundingDetailDto viewFriendsFundingDetail(Long fundingId, Long memberId) {

        List<FundingItem> fundingItems = fundingItemRepository.findAllByFundingId(fundingId);

        if (!fundingItems.isEmpty()) {
            List<FriendFundingItemDto> friendFundingItemList = fundingItems.stream()
                    .map(FriendFundingItemDto::fromEntity)
                    .toList();
            Funding funding = fundingItems.get(0).getFunding();

            List<ContributorDto> contributorList = contributorRepository.findByFundingId(fundingId)
                    .stream()
                    .map(ContributorDto::fromEntity)
                    .toList();

            int contributedPercent = funding.getCollectPrice() * 100 / funding.getTotalPrice();

            return FriendFundingDetailDto.fromEntity(friendFundingItemList, funding, contributorList,
                    contributedPercent);
        } else {
            throw new CommonException(NOT_FOUND_FUNDING);
        }
    }

    private List<CommonFriendFundingDto> getCommonFriendFundingList(Long memberId) {
        List<CommonFriendFundingDto> commonFriendFundingDtoList = new ArrayList<>();
        List<Relationship> relationshipList = relationshipRepository.findFriendByMemberId(memberId);
        if (relationshipList.isEmpty()) {
            return commonFriendFundingDtoList;
        }
        for (Relationship relationship : relationshipList) {
            Optional<Funding> friendFunding = fundingRepository.findByMemberIdAndStatus(
                    relationship.getFriend().getMemberId(), true);

            if (friendFunding.isEmpty()) {
                continue;
            }

            System.out.println(friendFunding.get().getFundingId());

            int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(),
                    friendFunding.get().getDeadline());
            String deadline = "D-" + leftDate;

            List<FundingItem> fundingItemList = fundingItemRepository.findFundingItemIdListByFunding(
                    friendFunding.get().getFundingId());
            List<FriendFundingPageItemDto> friendFundingPageItemDtoList = fundingItemList.stream()
                    .map(fundingItem -> FriendFundingPageItemDto.fromEntity(fundingItem.getItem())).toList();
            int totalPrice = friendFunding.get().getTotalPrice();

            if (totalPrice == 0) {
                throw new CommonException(INVALID_FUNDING_STATUS);
            }
            int fundingTotalPercent = friendFunding.get().getCollectPrice() * 100 / totalPrice;
            commonFriendFundingDtoList.add(CommonFriendFundingDto.fromEntity(
                    friendFunding.get(),
                    deadline,
                    fundingTotalPercent,
                    friendFundingPageItemDtoList
            ));
        }

        return commonFriendFundingDtoList;
    }

    public List<FriendFundingDto> getFriendFundingList(Long memberId) {
        List<CommonFriendFundingDto> commonFriendFundingDtoList = getCommonFriendFundingList(memberId);
        List<FriendFundingDto> friendFundingDtoList = new ArrayList<>();

        for (CommonFriendFundingDto commonFriendFundingDto : commonFriendFundingDtoList) {
            friendFundingDtoList.add(FriendFundingDto.fromEntity(commonFriendFundingDto));
        }

        return friendFundingDtoList;
    }

    @Transactional
    public CommonSuccessDto extendFunding(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_FUNDING));
        funding.extendDeadline(FundingConst.EXTEND_DEADLINE);
        return CommonSuccessDto.fromEntity(true);
    }

    public HomeViewDto getMainView(Long memberId) {
        Optional<Funding> funding = fundingRepository.findFundingInfo(memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
        // 사용자 정보: 이름, 프로필 이미지
        HomeMemberInfoDto homeMemberInfoDto = HomeMemberInfoDto.fromEntity(member);

        // 사용자 펀딩 내용: 펀딩 이름, 완료일
        HomeMyFundingStatusDto myFundingStatus = funding.map(this::getMyFundingStatusAndDeadLine).orElse(null);

        // 사용자 펀딩 상세: 펀딩 상품 이미지, 펀딩 진행률
        List<HomeMyFundingItemDto> homeMyFundingItemList = funding.map(this::getMyFundingItems).orElse(null);

        // 친구 펀딩: 이름, 프로필 이미지, 펀딩Id, 현재 펀딩 진행중인 상품 이미지, 펀딩 진행률, 펀딩 마감일
        List<HomeFriendFundingDto> homeFriendFundingList = getFriendFundingListByHome(memberId);

        // 상품 목록: 상품Id, 이름, 가격, 이미지, 브랜드명
        List<HomeItemDto> itemList = itemRepository.findAll().stream()
                .map(HomeItemDto::fromEntity)
                .toList();

        return HomeViewDto.fromEntity(homeMemberInfoDto, myFundingStatus, homeMyFundingItemList,
                homeFriendFundingList, itemList);
    }

    private List<HomeFriendFundingDto> getFriendFundingListByHome(Long memberId) {

        List<CommonFriendFundingDto> commonFriendFundingDtoList = getCommonFriendFundingList(memberId);
        List<HomeFriendFundingDto> friendFundingDtoList = new ArrayList<>();

        for (CommonFriendFundingDto commonFriendFundingDto : commonFriendFundingDtoList) {
            int collectPrice = commonFriendFundingDto.collectPrice();
            String nowFundingItemImageUrl = null;
            for (FriendFundingPageItemDto friendFundingPageItemDto : commonFriendFundingDto.friendFundingPageItemDtoList()) {
                int itemPrice = friendFundingPageItemDto.itemPrice();

                if (collectPrice >= itemPrice) {
                    collectPrice -= itemPrice;
                } else {
                    nowFundingItemImageUrl = friendFundingPageItemDto.itemImageUrl();
                }
            }
            friendFundingDtoList.add(HomeFriendFundingDto.fromEntity(
                    commonFriendFundingDto,
                    nowFundingItemImageUrl));
        }
        return friendFundingDtoList;
    }

    private List<HomeMyFundingItemDto> getMyFundingItems(Funding funding) {
        if (funding == null) {
            return null;
        }
        int collectPrice = funding.getCollectPrice();
        List<FundingItem> myFundingItems = funding.getFundingItems();
        List<HomeMyFundingItemDto> myFundingItemDtoList = new ArrayList<>();
        for (FundingItem myFundingItem : myFundingItems) {
            int itemPrice = myFundingItem.getItem().getItemPrice();
            int percent;
            if (collectPrice >= itemPrice) {
                collectPrice -= itemPrice;
                percent = 100;
            } else {
                percent = collectPrice * 100 / itemPrice;
            }
            HomeMyFundingItemDto homeMyFundingItemDto = HomeMyFundingItemDto.fromEntity(
                    myFundingItem, percent);
            myFundingItemDtoList.add(homeMyFundingItemDto);
        }
        return myFundingItemDtoList;
    }

    private HomeMyFundingStatusDto getMyFundingStatusAndDeadLine(Funding funding) {
        if (funding == null) {
            return null;
        }
        int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(), funding.getDeadline());
        String deadline = "D-" + leftDate;
        return HomeMyFundingStatusDto.fromEntity(funding, deadline);
    }

    public MyFundingStatusDto getMyFundingStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
        Optional<Funding> funding = fundingRepository.findByMemberIdAndStatus(member.getMemberId(), true);
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);
        if (funding.isEmpty()) {
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

        List<MyPageFundingDetailHistoryDto> myPageFundingDetailHistoryDtos = fundings.stream()
                .map(funding -> {
                    int contributors = contributorRepository.countContributorsForFunding(funding.getFundingId());
                    return MyPageFundingDetailHistoryDto.fromEntity(funding, contributors);
                })
                .toList();

        return MyFundingHistoryDto.fromEntity(myPageMemberDto, myPageFundingDetailHistoryDtos);
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

    public FriendFundingHistoryDto getFriendFundingHistory(Long memberId) {

        List<FriendFundingContributionDto> friendFundingContributionDtoList =
                contributorRepository.findAllByMemberId(memberId).stream()
                        .map(contributor -> {
                            Funding contributeFunding = contributor.getFunding();
                            return FriendFundingContributionDto.fromEntity(contributor, contributeFunding);
                        })
                        .toList();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);

        return FriendFundingHistoryDto.fromEntity(myPageMemberDto, friendFundingContributionDtoList);
    }
}
