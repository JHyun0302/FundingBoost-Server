package kcs.funding.fundingboost.domain.service.pay;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.FundingPaymentDto;
import kcs.funding.fundingboost.domain.dto.request.MyPayDto;
import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;
import kcs.funding.fundingboost.domain.dto.response.MyFundingPayViewDto;
import kcs.funding.fundingboost.domain.dto.response.MyNowOrderPayViewDto;
import kcs.funding.fundingboost.domain.dto.response.MyOrderPayViewDto;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.FundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.GiftHubItemRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPayService {

    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final FundingItemRepository fundingItemRepository;
    private final FundingRepository fundingRepository;
    private final GiftHubItemRepository giftHubItemRepository;
    private final ItemRepository itemRepository;

    public MyFundingPayViewDto myFundingPayView(Long fundingItemId, Long memberId) {

        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_FUNDING_ITEM));

        List<DeliveryDto> deliveryDtoList = deliveryRepository.findAllByMemberId(memberId)
                .stream()
                .map(DeliveryDto::fromEntity)
                .toList();

        if (!fundingItem.isFinishedStatus()) {
            throw new CommonException(ErrorCode.INVALID_FUNDINGITEM_STATUS);
        }

        if (fundingItem.getFunding().isFundingStatus()) {
            throw new CommonException(ErrorCode.ONGOING_FUNDING_ERROR);
        }

        if (!fundingItem.getFunding().getMember().getMemberId().equals(memberId)) {
            throw new CommonException(ErrorCode.BAD_REQUEST_PARAMETER);
        }

        return MyFundingPayViewDto.fromEntity(fundingItem, deliveryDtoList);
    }

    public MyOrderPayViewDto myOrderPayView(List<Long> itemIds, Long memberId) {

        List<DeliveryDto> deliveryDtoList = deliveryRepository.findAllByMemberId(memberId)
                .stream()
                .map(DeliveryDto::fromEntity)
                .toList();

        List<Long> giftHubItemIds = giftHubItemRepository.findGiftHubItemByMemberIdAndItemIds(memberId, itemIds)
                .stream().map(GiftHubItem::getGiftHunItemId)
                .toList();

        List<ItemDto> itemDtoList = itemIds.stream()
                .flatMap(i -> itemRepository.findById(i).stream())
                .map(item -> ItemDto.fromEntity(item.getItemId(),
                        item.getItemImageUrl(),
                        item.getItemName(),
                        item.getOptionName(),
                        item.getItemPrice()))
                .toList();

        int point = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER))
                .getPoint();

        return MyOrderPayViewDto.fromEntity(itemDtoList, giftHubItemIds, deliveryDtoList, point);
    }

    public MyNowOrderPayViewDto MyOrderNowPayView(Long itemId, Long memberId) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ITEM));
        ItemDto itemDto = ItemDto.fromEntity(item.getItemId(),
                item.getItemImageUrl(),
                item.getItemName(),
                item.getOptionName(),
                item.getItemPrice()
        );

        int point = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER))
                .getPoint();

        List<DeliveryDto> deliveryDtoList = deliveryRepository.findAllByMemberId(memberId)
                .stream()
                .map(DeliveryDto::fromEntity)
                .toList();

        return MyNowOrderPayViewDto.fromEntity(itemDto, deliveryDtoList, point);
    }

    @Transactional
    public CommonSuccessDto payMyItem(MyPayDto paymentDto, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        deductPointsIfPossible(findMember, paymentDto.usingPoint());
        return CommonSuccessDto.fromEntity(true);
    }

    @Transactional
    public CommonSuccessDto payMyFunding(FundingPaymentDto fundingPaymentDto, Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MEMBER));
        FundingItem fundingItem = fundingItemRepository.findById(fundingPaymentDto.fundingItemId()).orElseThrow();
        deductPointsIfPossible(findMember, fundingPaymentDto.usingPoint());
        fundingItem.finishFunding();
        return CommonSuccessDto.fromEntity(true);
    }

    private void deductPointsIfPossible(Member member, int points) {
        if (member.getPoint() - points >= 0) {
            member.minusPoint(points);
        } else {
            throw new RuntimeException("point가 부족합니다");
        }
    }
}
