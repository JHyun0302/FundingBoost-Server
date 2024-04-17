package kcs.funding.fundingboost.domain.service;

import java.util.List;
import java.util.stream.IntStream;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.ContributorDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingDetailDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.repository.ContributorRepository;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.exception.ErrorCode;
import kcs.funding.fundingboost.domain.repository.FundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
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

    public List<FundingRegistrationItemDto> getFundingRegister(List<Long> registerFundingBringItemDto, Long memberId) {

        Funding funding = fundingRepository.findByMemberIdAndStatus(memberId, true);

        if (funding != null) {
            throw new CommonException(ErrorCode.ALREADY_EXIST_FUNDING);
        }

        return IntStream.range(0, registerFundingBringItemDto.size())
                .mapToObj(i -> FundingRegistrationItemDto.createFundingRegistrationItemDto(
                        itemRepository.findById(registerFundingBringItemDto.get(i))
                                .orElseThrow(() -> new RuntimeException("Item not found")),
                        (long) i + 1)).toList();
    }

    @Transactional
    public CommonSuccessDto putFundingAndFundingItem(Long memberId, RegisterFundingDto registerFundingDto) {

        List<Long> registerFundingItemList = registerFundingDto.itemIdList();

        List<Item> itemList = registerFundingItemList.stream()
                .map(itemIdList -> itemRepository.findById(itemIdList)
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

        for (int i = 0; i < registerFundingItemList.size(); i++) {
            FundingItem fundingItem = FundingItem.createFundingItem(
                    funding,
                    itemRepository.findById(registerFundingItemList.get(i))
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

        List<FundingItem> fundingItems = fundingItemRepository.findAllByFundingId(fundingId);
        List<FriendFundingItemDto> friendFundingItemList = fundingItems.stream().map(FriendFundingItemDto::fromEntity).toList();

        Funding funding = fundingItems.get(0).getFunding();

        List<ContributorDto> contributorList = contributorRepository.findByFundingId(fundingId)
                .stream()
                .map(ContributorDto::fromEntity)
                .toList();

        int contributedPercent = 0;
        if (funding.getTotalPrice() > 0) {
            contributedPercent = funding.getCollectPrice() / funding.getTotalPrice() * 100;
        } else {
            throw new RuntimeException("펀딩에 담긴 상품이 없거나, 상품의 가격이 이상합니다.");
        }

        return FriendFundingDetailDto.fromEntity(friendFundingItemList, funding, contributorList, contributedPercent);

    }
}
