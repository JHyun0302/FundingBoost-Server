package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.dto.response.GiftHubDto.createGiftHubDto;

import java.util.List;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.AddGiftHubDto;
import kcs.funding.fundingboost.domain.dto.request.ItemQuantityDto;
import kcs.funding.fundingboost.domain.dto.response.GiftHubDto;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
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
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<GiftHubItem> giftHubItems = giftHubItemRepository.findGiftHubItemsByMember(member);

        return giftHubItems.stream()
                .map(giftHubItem -> createGiftHubDto(giftHubItem.getItem(), giftHubItem))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommonSuccessDto addGiftHub(Long itemId, AddGiftHubDto addGiftHubDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Member member = memberRepository.findById(addGiftHubDto.memberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        GiftHubItem giftHubItem = GiftHubItem.createGiftHubItem(addGiftHubDto.quantity(), item, member);
        GiftHubItem saveGiftHubItem = giftHubItemRepository.save(giftHubItem);

        if (saveGiftHubItem != null) {
            return CommonSuccessDto.fromEntity(true);
        } else {
            throw new RuntimeException("Saving GiftHubItem failed");
        }
    }

    @Transactional
    public CommonSuccessDto updateItem(Long gifthubItemId, ItemQuantityDto itemQuantity) {
        GiftHubItem giftHubItem = giftHubItemRepository.findById(gifthubItemId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_GIFTHUB_ITEM));
        giftHubItem.updateQuantity(itemQuantity.quantity());
        return CommonSuccessDto.fromEntity(true);
    }
}
