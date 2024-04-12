package kcs.funding.fundingboost.domain.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.HomeFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.HomeMemberInfoDto;
import kcs.funding.fundingboost.domain.dto.response.HomeMyFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.HomeMyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.HomeViewDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final FundingRepository fundingRepository;
    private final RelationshipRepositoryRepository relationshipRepository;
    private final ItemRepository itemRepository;


    public HomeViewDto getMainView(Long memberId) {
        Funding funding = fundingRepository.findFundingInfo(memberId);

        // 사용자 정보: 이름, 프로필 이미지
        HomeMemberInfoDto homeMemberInfoDto = HomeMemberInfoDto.fromEntity(funding.getMember());

        // 사용자 펀딩 내용: 펀딩 이름, 완료일
        HomeMyFundingStatusDto myFundingStatus = getMyFundingStatus(funding);

        // 사용자 펀딩 상세: 펀딩 상품 이미지, 펀딩 진행률
        List<HomeMyFundingItemDto> homeMyFundingItemList = getMyFundingItems(funding);

        // 친구 펀딩: 이름, 프로필 이미지, 펀딩Id, 현재 펀딩 진행중인 상품 이미지, 펀딩 진행률, 펀딩 마감일
        List<HomeFriendFundingDto> homeFriendFundingList = getFriendFundingList(memberId, funding);

        // 상품 목록: 상품Id, 이름, 가격, 이미지, 브랜드명
        List<ItemDto> itemList = itemRepository.findAll().stream()
            .map(ItemDto::fromEntity)
            .toList();

        return HomeViewDto.fromEntity(homeMemberInfoDto, myFundingStatus, homeMyFundingItemList,
            homeFriendFundingList, itemList);
    }

    private List<HomeFriendFundingDto> getFriendFundingList(Long memberId, Funding funding) {
        List<Member> friends = relationshipRepository.findFriendByMemberId(memberId);
        List<HomeFriendFundingDto> friendFundingDtoList = new ArrayList<>();

        for (Member friend : friends) {
            Funding friendFunding = fundingRepository.findFundingInfo(friend.getMemberId());
            int collectPrice = friendFunding.getCollectPrice();
            int percent = collectPrice / funding.getTotalPrice();
            List<FundingItem> fundingItems = friendFunding.getFundingItems();
            for (FundingItem fundingItem : fundingItems) {
                int itemPrice = fundingItem.getItem().getItemPrice();
                String nowFundingItemImageUrl = null;
                if (collectPrice >= itemPrice) {
                    collectPrice -= itemPrice;
                } else {
                    nowFundingItemImageUrl = fundingItem.getItem().getItemImageUrl();
                }

                int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(), friendFunding.getDeadline());
                String deadline = "D-" + leftDate;

                HomeFriendFundingDto homeFriendFundingDto = HomeFriendFundingDto.fromEntity(friendFunding,
                    nowFundingItemImageUrl, percent,
                    deadline);
                friendFundingDtoList.add(homeFriendFundingDto);
            }
        }
        return friendFundingDtoList;
    }

    private static List<HomeMyFundingItemDto> getMyFundingItems(Funding funding) {
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
                percent = (int) collectPrice / itemPrice;
            }
            HomeMyFundingItemDto homeMyFundingItemDto = HomeMyFundingItemDto.fromEntity(myFundingItem, percent);
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
