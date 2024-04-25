package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.ALREADY_EXIST_FUNDING;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_FUNDINGITEM_STATUS;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_FUNDING_OR_PRICE;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_FUNDING_STATUS;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_POINT_LACK;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_DELIVERY;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_FUNDING;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_ITEM;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.PayRemainDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.CommonFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.ContributorDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingDetailDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPageItemDto;
import kcs.funding.fundingboost.domain.dto.response.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.entity.OrderItem;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.Contributor.ContributorRepository;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.FundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.OrderItemRepository;
import kcs.funding.fundingboost.domain.repository.OrderRepository;
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
public class FundingService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final FundingItemRepository fundingItemRepository;
    private final ContributorRepository contributorRepository;
    private final RelationshipRepository relationshipRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public List<FundingRegistrationItemDto> getFundingRegister(List<Long> registerFundingBringItemDto, Long memberId) {

        Optional<Funding> funding = fundingRepository.findByMemberIdAndStatus(memberId, true);

        if (funding.isPresent()) {
            throw new CommonException(ALREADY_EXIST_FUNDING);
        }

        return IntStream.range(0, registerFundingBringItemDto.size())
                .mapToObj(i -> FundingRegistrationItemDto.createFundingRegistrationItemDto(
                        itemRepository.findById(registerFundingBringItemDto.get(i))
                                .orElseThrow(() -> new CommonException(NOT_FOUND_ITEM)),
                        (long) i + 1)).toList();
    }

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
                registerFundingDto.deadline());

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
        List<FriendFundingItemDto> friendFundingItemList = fundingItems.stream().map(FriendFundingItemDto::fromEntity)
                .toList();

        Funding funding = fundingItems.get(0).getFunding();

        List<ContributorDto> contributorList = contributorRepository.findByFundingId(fundingId)
                .stream()
                .map(ContributorDto::fromEntity)
                .toList();

        int contributedPercent = 0;
        if (funding.getTotalPrice() > 0) {
            contributedPercent = funding.getCollectPrice() / funding.getTotalPrice() * 100;
        } else {
            throw new CommonException(INVALID_FUNDING_OR_PRICE);
        }

        return FriendFundingDetailDto.fromEntity(friendFundingItemList, funding, contributorList, contributedPercent);
    }

    public List<CommonFriendFundingDto> getCommonFriendFundingList(Long memberId) {
        List<CommonFriendFundingDto> commonFriendFundingDtoList = new ArrayList<>();
        List<Relationship> relationshipList = relationshipRepository.findFriendByMemberId(memberId);
        for (Relationship relationship : relationshipList) {
            Optional<Funding> friendFunding = fundingRepository.findByMemberIdAndStatus(
                    relationship.getFriend().getMemberId(),
                    true);

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

    @Transactional
    public CommonSuccessDto payRemain(Long fundingItemId, PayRemainDto payRemainDto, Long memberId) {

        FundingItem fundingItem = fundingItemRepository.findFundingItemAndItemByFundingItemId(fundingItemId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
        Delivery delivery = deliveryRepository.findById(payRemainDto.deliveryId())
                .orElseThrow(() -> new CommonException(NOT_FOUND_DELIVERY));

        if (!fundingItem.isFinishedStatus()) {
            throw new CommonException(INVALID_FUNDINGITEM_STATUS);
        } else {
            fundingItem.finishFunding();
        }

        if (member.getPoint() - payRemainDto.usingPoint() >= 0) {
            member.minusPoint(payRemainDto.usingPoint());
        } else {
            throw new CommonException(INVALID_POINT_LACK);
        }

        Order order = Order.createOrder(fundingItem.getItem().getItemPrice(), member, delivery);
        OrderItem orderItem = OrderItem.createOrderItem(order, fundingItem.getItem(), 1);
        orderRepository.save(order);
        orderItemRepository.save(orderItem);

        return CommonSuccessDto.fromEntity(true);
    }
}
