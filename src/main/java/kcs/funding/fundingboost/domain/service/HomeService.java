package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.CommonFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPageItemDto;
import kcs.funding.fundingboost.domain.dto.response.HomeFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.HomeItemDto;
import kcs.funding.fundingboost.domain.dto.response.HomeMemberInfoDto;
import kcs.funding.fundingboost.domain.dto.response.HomeMyFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.HomeMyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.HomeViewDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

    private final FundingRepository fundingRepository;
    private final RelationshipRepository relationshipRepository;
    private final ItemRepository itemRepository;
    private final FundingService fundingService;
    private final MemberRepository memberRepository;


    public HomeViewDto getMainView(Long memberId) {
        Funding funding = fundingRepository.findFundingInfo(memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
        // 사용자 정보: 이름, 프로필 이미지
        HomeMemberInfoDto homeMemberInfoDto = HomeMemberInfoDto.fromEntity(member);

        // 사용자 펀딩 내용: 펀딩 이름, 완료일
        if (funding == null) {
            HomeMyFundingStatusDto myFundingStatus = null;
        }
        HomeMyFundingStatusDto myFundingStatus = getMyFundingStatus(funding);

        // 사용자 펀딩 상세: 펀딩 상품 이미지, 펀딩 진행률
        List<HomeMyFundingItemDto> homeMyFundingItemList = getMyFundingItems(funding);

        // 친구 펀딩: 이름, 프로필 이미지, 펀딩Id, 현재 펀딩 진행중인 상품 이미지, 펀딩 진행률, 펀딩 마감일
        List<HomeFriendFundingDto> homeFriendFundingList = getFriendFundingList(memberId);

        // 상품 목록: 상품Id, 이름, 가격, 이미지, 브랜드명
        List<HomeItemDto> itemList = itemRepository.findAll().stream()
                .map(HomeItemDto::fromEntity)
                .toList();

        return HomeViewDto.fromEntity(homeMemberInfoDto, myFundingStatus, homeMyFundingItemList,
                homeFriendFundingList, itemList);
    }

    private List<HomeFriendFundingDto> getFriendFundingList(Long memberId) {

        List<CommonFriendFundingDto> commonFriendFundingDtoList = fundingService.getCommonFriendFundingList(memberId);
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
                percent = (int) collectPrice / itemPrice * 100;
            }
            HomeMyFundingItemDto homeMyFundingItemDto = HomeMyFundingItemDto.fromEntity(
                    myFundingItem, percent);
            myFundingItemDtoList.add(homeMyFundingItemDto);
        }
        return myFundingItemDtoList;
    }

    private HomeMyFundingStatusDto getMyFundingStatus(Funding funding) {
        int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(), funding.getDeadline());
        String deadline = "D-" + leftDate;
        return HomeMyFundingStatusDto.fromEntity(funding, deadline);
    }
}
