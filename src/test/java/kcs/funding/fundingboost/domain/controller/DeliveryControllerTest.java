package kcs.funding.fundingboost.domain.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.MyPageDeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.MyPageDeliveryManageDto;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
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
    }

    @DisplayName("배송지 관리 조회")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "구태형, 서울 금천구 가산디지털1로 189 (주)LG 가산 디지털센터 12층, 010-1111-2222",
            "맹인호, 경기도 화성시, 010-3333-4444",
    })
    void viewMyDeliveryManagement(String customerName, String address, String phoneNumber) throws Exception {
        // given
        MyPageMemberDto myPageMemberDto = new MyPageMemberDto(member.getNickName(), member.getEmail(),
                member.getProfileImgUrl(), member.getPoint());
        List<MyPageDeliveryDto> myPageDeliveryDtoList = Collections.singletonList(
                new MyPageDeliveryDto(customerName, address, phoneNumber));

        MyPageDeliveryManageDto expectedResponse = MyPageDeliveryManageDto.fromEntity(myPageMemberDto,
                myPageDeliveryDtoList);
        given(deliveryService.getMyDeliveryManageList(member.getMemberId())).willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/v1/delivery")
                        .param("memberId", member.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.myPageMemberDto.nickname").value(myPageMemberDto.nickname()))
                .andExpect(jsonPath("$.data.myPageMemberDto.email").value(myPageMemberDto.email()))
                .andExpect(jsonPath("$.data.myPageMemberDto.profileImgUrl").value(myPageMemberDto.profileImgUrl()))
                .andExpect(jsonPath("$.data.myPageMemberDto.point").value(myPageMemberDto.point()))
                .andExpect(jsonPath("$.data.myPageDeliveryDtoList", hasSize(myPageDeliveryDtoList.size())))
                .andExpect(jsonPath("$.data.myPageDeliveryDtoList[0].customerName").value(customerName))
                .andExpect(jsonPath("$.data.myPageDeliveryDtoList[0].address").value(address))
                .andExpect(jsonPath("$.data.myPageDeliveryDtoList[0].phoneNumber").value(phoneNumber));
    }

    private static Member createMember() {
        return Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }
}