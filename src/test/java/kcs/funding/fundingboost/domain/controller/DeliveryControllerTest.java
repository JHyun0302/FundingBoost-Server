package kcs.funding.fundingboost.domain.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.MyPageDeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.MyPageDeliveryManageDto;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private DeliveryService deliveryService;

    private Member member;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = createMember();
        Field memberId = member.getClass().getDeclaredField("memberId");
        memberId.setAccessible(true);
        memberId.set(member, 1L);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @DisplayName("배송지 관리 조회")
    @Test
    void viewMyDeliveryManagement() throws Exception {
        // given
        MyPageMemberDto myPageMemberDto = new MyPageMemberDto(member.getNickName(), member.getEmail(),
                member.getProfileImgUrl(), member.getPoint());
        List<MyPageDeliveryDto> myPageDeliveryDtoList = new ArrayList<>();
        MyPageDeliveryManageDto expectedResponse = MyPageDeliveryManageDto.fromEntity(myPageMemberDto,
                myPageDeliveryDtoList);
        given(deliveryService.getMyDeliveryManageList(member.getMemberId())).willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/v1/delivery")
                        .param("memberId", member.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    private static Member createMember() {
        return Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }
}