package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.dto.response.GiftHubDto.createGiftHubDto;

import java.util.List;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.response.GiftHubDto;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.GiftHubItemRepository;
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

    private final MemberRepository memberRepository;


    public List<GiftHubDto> getGiftHub(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<GiftHubItem> giftHubItems = giftHubItemRepository.findGiftHubItemsByMember(member);

        return giftHubItems.stream()
                .map(giftHubItem -> createGiftHubDto(giftHubItem.getItem(), giftHubItem))
                .collect(Collectors.toList());
    }
}
