package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.dto.response.GiftHubDto.createGiftHubDto;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.INTERNAL_SAVE_ERROR;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_ITEM;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;

import java.util.List;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.AddGiftHubDto;
import kcs.funding.fundingboost.domain.dto.response.GiftHubDto;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.GiftHubItemRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class GiftHubItemService {
    private final GiftHubItemRepository giftHubItemRepository;

    private final ItemRepository itemRepository;

    private final MemberRepository memberRepository;


    public List<GiftHubDto> getGiftHub(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));

        List<GiftHubItem> giftHubItems = giftHubItemRepository.findGiftHubItemsByMember(member);

        return giftHubItems.stream()
                .map(giftHubItem -> createGiftHubDto(giftHubItem.getItem(), giftHubItem))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommonSuccessDto addGiftHub(Long itemId, AddGiftHubDto addGiftHubDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_ITEM));

        Member member = memberRepository.findById(addGiftHubDto.memberId())
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));

        GiftHubItem giftHubItem = GiftHubItem.createGiftHubItem(addGiftHubDto.quantity(), item, member);
        GiftHubItem saveGiftHubItem = giftHubItemRepository.save(giftHubItem);

        if (saveGiftHubItem != null) {
            return CommonSuccessDto.fromEntity(true);
        } else {
            throw new CommonException(INTERNAL_SAVE_ERROR);
        }
    }
}
