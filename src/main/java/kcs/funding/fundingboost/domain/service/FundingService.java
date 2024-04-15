package kcs.funding.fundingboost.domain.service;

import jakarta.transaction.Transactional;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.request.RegisterFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.FundingRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingService {

    private static ItemRepository itemRepository;
    private static MemberRepository memberRepository;
    private final FundingRepository fundingRepository;
    private final FundingItemRepository fundingItemRepository;

    public List<FundingRegistrationItemDto> getFundingRegister(List<Long> itemList){

        return IntStream.range(0, itemList.size())
                .mapToObj(i -> FundingRegistrationItemDto.createFundingRegistrationItemDto(itemRepository.getReferenceById(itemList.get(i)), (long) i + 1))
                .toList();
    }

    @Transactional
    public void putFundingAndFundingItem(Long memberId, RegisterFundingDto registerFundingDto){

        List<RegisterFundingItemDto> registerFundingItemDtoList = registerFundingDto.registerFundingItemDtoList();

        List<Item> itemList = registerFundingItemDtoList.stream()
                .map(registerFundingItemDto -> itemRepository.getReferenceById(registerFundingItemDto.itemId()))
                .toList();

        int sum = 0;
        for(Item item : itemList){
            sum += item.getItemPrice();
        }
        Funding funding = Funding.createFunding(memberRepository.getReferenceById(memberId), registerFundingDto.fundingMessage(), Tag.getTag(registerFundingDto.tag()), sum, registerFundingDto.deadline());
        fundingRepository.save(funding);
        for(int i=0; i<registerFundingItemDtoList.size(); i++){
            FundingItem fundingItem = FundingItem.createFundingItem(funding, itemRepository.getReferenceById(registerFundingItemDtoList.get(i).itemId()), i+1);
            fundingItemRepository.save(fundingItem);
        }
        // CommonSuccessDto return
    }
}
