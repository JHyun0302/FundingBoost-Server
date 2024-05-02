package kcs.funding.fundingboost.domain.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.myPage.myFundingStatus.TransformPointDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Funding funding;
    private Item item1;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = createMember();
        Field memberId = member.getClass().getDeclaredField("memberId");
        memberId.setAccessible(true);
        memberId.set(member, 1L);
        funding = createFunding();
        Field fundingId = funding.getClass().getDeclaredField("fundingId");
        fundingId.setAccessible(true);
        fundingId.set(funding, 1L);

        item1 = createItemId(
                1L,
                "NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션",
                61000,
                "https://img1.kakaocdn.net/...",
                "샤넬",
                "뷰티",
                "00:00");

        FundingItem fundingItem = FundingItem.createFundingItem(funding, item1, 1);
        Field fundingItems = funding.getClass().getDeclaredField("fundingItems");
        fundingItems.setAccessible(true);
        fundingItems.set(funding, List.of(fundingItem));
    }

    @DisplayName("포인트 전환")
    @Test
    void exchangePoint() throws Exception {
        TransformPointDto transformPointDto = new TransformPointDto(funding.getFundingId());
        CommonSuccessDto commonSuccessDto = new CommonSuccessDto(true);

        given(memberService.exchangePoint(transformPointDto)).willReturn(commonSuccessDto);

        String content = objectMapper.writeValueAsString(transformPointDto);
        System.out.println(content);
        mockMvc.perform(patch("/api/v1/member/point")
                        .param("memberId", String.valueOf(member.getMemberId()))
                        .contentType(APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isSuccess").value(true));
    }

    private Funding createFunding() {
        Funding funding = Funding.createFundingForTest(
                member, "생일 선물 주세용", Tag.BIRTHDAY, 112000,
                10000, LocalDateTime.of(2024, 4, 30, 23, 59));
        return funding;
    }

    private static Member createMember() {
        return Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }

    private static Item createItemId(
            Long itemId1,
            String itemName,
            int itemPrice,
            String itemImageUrl,
            String brandName,
            String category,
            String optionName
    ) throws NoSuchFieldException, IllegalAccessException {
        Item item = Item.createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
        Field itemId = item.getClass().getDeclaredField("itemId");
        itemId.setAccessible(true);
        itemId.set(item, itemId1);
        return item;
    }
}