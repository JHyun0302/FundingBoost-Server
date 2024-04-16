package kcs.funding.fundingboost.domain.service;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.ContributorDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingDetailDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.entity.*;
import kcs.funding.fundingboost.domain.repository.ContributorRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

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

    public List<FundingRegistrationItemDto> getFundingRegister(List<Long> itemList){

        return IntStream.range(0, itemList.size())
                .mapToObj(i -> FundingRegistrationItemDto.createFundingRegistrationItemDto(
                        itemRepository.findById(itemList.get(i))
                                .orElseThrow(()-> new RuntimeException("Item not found")),
                        (long) i + 1)).toList();
    }

    @Transactional
    public CommonSuccessDto putFundingAndFundingItem(Long memberId, RegisterFundingDto registerFundingDto) {

        List<RegisterFundingItemDto> registerFundingItemDtoList = registerFundingDto.registerFundingItemDtoList();

        List<Item> itemList = registerFundingItemDtoList.stream()
                .map(registerFundingItemDto -> itemRepository.findById(registerFundingItemDto.itemId())
                        .orElseThrow(() -> new RuntimeException("Item Not Found"))).toList();

        int sum = 0;
        for (Item item : itemList) {
            sum += item.getItemPrice();
        }

        Funding funding = Funding.createFunding(memberRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Member Not Found")),
                registerFundingDto.fundingMessage(),
                Tag.getTag(registerFundingDto.tag()),
                sum,
                registerFundingDto.deadline());

        fundingRepository.save(funding);

        for (int i = 0; i < registerFundingItemDtoList.size(); i++) {
            FundingItem fundingItem = FundingItem.createFundingItem(
                    funding,
                    itemRepository.findById(registerFundingItemDtoList.get(i).itemId())
                            .orElseThrow(() -> new RuntimeException("Item Not Found")),
                    i + 1);
            fundingItemRepository.save(fundingItem);
        }

        return CommonSuccessDto.fromEntity(true);
    }

    public CommonSuccessDto terminateFunding(Long fundingId) {
          Funding funding = fundingRepository.findById(fundingId)
                  .orElseThrow(() -> new RuntimeException("Funding not found"));
          funding.terminate();
          return CommonSuccessDto.fromEntity(true);
    }

    public FriendFundingDetailDto viewFreindsFundingDetail(Long fundingId, Long memberId) {

        List<FriendFundingItemDto> friendFundingItemList = fundingItemRepository.findAllByFundingId(fundingId)
                .stream()
                .map(fi -> FriendFundingItemDto.fromEntity(fi))
                .toList();

        List<ContributorDto> contributorList = contributorRepository.findByFundingId(fundingId)
                .stream()
                .map(c -> ContributorDto.fromEntity(c))
                .toList();

        Member friend = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("member not found"));
        String friendName = friend.getNickName();
        String friendProfile = friend.getProfileImgUrl();


        Funding funding = fundingRepository.findById(fundingId).orElseThrow(() -> new RuntimeException("funding not found"));
        LocalDateTime deadline = funding.getDeadline();
        int totalPrice = funding.getTotalPrice();
        int collectPrice = funding.getCollectPrice();
        Tag fundingTag = funding.getTag();
        String fundingMessage = funding.getMessage();

        int contributedPercent = 0;
        if (totalPrice > 0) {
            contributedPercent = collectPrice / totalPrice * 100;
        } else {
            throw new RuntimeException("펀딩에 담긴 상품이 없거나, 상품의 가격이 이상합니다.");
        }

        return FriendFundingDetailDto.fromEntity(friendFundingItemList, contributorList, friendName,friendProfile, deadline, contributedPercent, fundingTag, fundingMessage);

    }
}
