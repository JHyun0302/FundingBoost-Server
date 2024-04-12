package kcs.funding.fundingboost.domain.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.MemberDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.ViewItemDto;
import kcs.funding.fundingboost.domain.dto.response.ViewMainDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
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


    public ViewMainDto getMainView(Long memberId) {
        Funding funding = fundingRepository.findFundingInfo(memberId);

        MemberDto memberDto = MemberDto.fromEntity(funding.getMember());

        MyFundingStatusDto myFundingStatus = getMyFundingStatus(funding);

        List<MyFundingItemDto> myFundingItemList = getMyFundingItems(funding);

        List<FriendFundingDto> friendFundingList = getFriendFundingList(memberId, funding);

        List<ViewItemDto> items = itemRepository.findAll().stream()
            .map(ViewItemDto::fromEntity)
            .toList();

        return ViewMainDto.fromEntity(memberDto, myFundingStatus, myFundingItemList,
            friendFundingList, items);
    }

    private List<FriendFundingDto> getFriendFundingList(Long memberId, Funding funding) {
        List<Member> friends = relationshipRepository.findFriendByMemberId(memberId);
        List<FriendFundingDto> friendFundingListDto = new ArrayList<>();

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

                FriendFundingDto friendFundingDto = FriendFundingDto.fromEntity(friendFunding,
                    nowFundingItemImageUrl, percent,
                    deadline);
                friendFundingListDto.add(friendFundingDto);
            }
        }
        return friendFundingListDto;
    }

    private static List<MyFundingItemDto> getMyFundingItems(Funding funding) {
        int collectPrice = funding.getCollectPrice();
        List<FundingItem> myFundingItems = funding.getFundingItems();
        List<MyFundingItemDto> myFundingItemListDto = new ArrayList<>();
        for (FundingItem myFundingItem : myFundingItems) {
            int itemPrice = myFundingItem.getItem().getItemPrice();
            int percent;
            if (collectPrice >= itemPrice) {
                collectPrice -= itemPrice;
                percent = 100;
            } else {
                percent = (int) collectPrice / itemPrice;
            }
            MyFundingItemDto myFundingItemDto = MyFundingItemDto.fromEntity(myFundingItem, percent);
            myFundingItemListDto.add(myFundingItemDto);
        }
        return myFundingItemListDto;
    }

    private MyFundingStatusDto getMyFundingStatus(Funding funding) {
        int leftDate = (int) ChronoUnit.DAYS.between(LocalDate.now(), funding.getDeadline());
        String deadline = "D-" + leftDate;
        return MyFundingStatusDto.fromEntity(funding, deadline);
    }
}
