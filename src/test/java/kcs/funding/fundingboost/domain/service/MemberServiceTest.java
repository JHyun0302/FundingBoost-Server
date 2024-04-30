package kcs.funding.fundingboost.domain.service;


import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_FUNDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.myPage.myFundingStatus.TransformPointDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {

    @Mock
    private FundingRepository fundingRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private Funding funding;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = createMember();
        Field memberId = member.getClass().getDeclaredField("memberId");
        memberId.setAccessible(true);
        memberId.set(member, 1L);
        funding = createFunding(member);
        Field fundingId = funding.getClass().getDeclaredField("fundingId");
        fundingId.setAccessible(true);
        fundingId.set(funding, 1L);

        Item item1 = Item.createItem(
                "NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션",
                61000,
                "https://img1.kakaocdn.net/...",
                "샤넬",
                "뷰티",
                "00:00"
        );

        Item item2 = Item.createItem(
                "NEW 루쥬 코코 밤(+샤넬 기프트 카드)",
                51000,
                "https://img1.kakaocdn.net/...",
                "샤넬",
                "뷰티",
                "934 코랄린 [NEW]"
        );
        FundingItem fundingItem1 = FundingItem.createFundingItem(funding, item1, 1);
        FundingItem fundingItem2 = FundingItem.createFundingItem(funding, item2, 2);
        Field fundingItems = funding.getClass().getDeclaredField("fundingItems");
        List<FundingItem> fundingItemList = new ArrayList<>();
        fundingItemList.add(fundingItem1);
        fundingItemList.add(fundingItem2);
        fundingItems.setAccessible(true);
        fundingItems.set(funding, fundingItemList);
    }

    @DisplayName("포인트 전환 성공")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @ValueSource(ints = {10000, 20000, 30000})
    void exchangePoint(int myPoint) throws NoSuchFieldException, IllegalAccessException {
        //given
        TransformPointDto transformPointDto = new TransformPointDto(funding.getFundingId());
        when(fundingRepository.findMemberByFundingId(transformPointDto.fundingId())).thenReturn(funding);
        Field memberPoint = member.getClass().getDeclaredField("point");
        memberPoint.setAccessible(true);
        memberPoint.set(member, myPoint);

        //when
        CommonSuccessDto commonSuccessDto = memberService.exchangePoint(transformPointDto);
        verify(fundingRepository).findMemberByFundingId(transformPointDto.fundingId());

        //then
        assertTrue(commonSuccessDto.isSuccess());
        verify(fundingRepository).findMemberByFundingId(transformPointDto.fundingId());
        assertEquals(myPoint + funding.getCollectPrice(), member.getPoint());
    }

    @DisplayName("포인트 전환 실패 - 펀딩을 찾을 수 없음")
    @Test
    void exchangePoint_FundingNotFound() {
        TransformPointDto transformPointDto = new TransformPointDto(funding.getFundingId());
        when(fundingRepository.findMemberByFundingId(anyLong())).thenReturn(null);

        //when
        CommonException exception = assertThrows(CommonException.class, () -> {
            memberService.exchangePoint(transformPointDto);
        });// 펀딩을 찾지 못했으므로 실패해야 함

        //then
        assertEquals(NOT_FOUND_FUNDING.getMessage(), exception.getMessage());
    }


    private static Member createMember() {
        return Member.createMember("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }

    private static Funding createFunding(Member member) {
        return Funding.createFundingForTest(
                member,
                "생일축하해줘",
                Tag.getTag("생일"),
                112000,
                50000,
                LocalDateTime.now().minusDays(14)
        );
    }
}