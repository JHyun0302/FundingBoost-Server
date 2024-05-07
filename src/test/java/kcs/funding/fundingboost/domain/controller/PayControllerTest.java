package kcs.funding.fundingboost.domain.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.pay.friendFundingPay.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.response.pay.friendFundingPay.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.entity.OrderItem;
import kcs.funding.fundingboost.domain.model.DeliveryFixture;
import kcs.funding.fundingboost.domain.model.FundingFixture;
import kcs.funding.fundingboost.domain.model.ItemFixture;
import kcs.funding.fundingboost.domain.model.MemberFixture;
import kcs.funding.fundingboost.domain.model.OrderFixture;
import kcs.funding.fundingboost.domain.model.OrderItemFixture;
import kcs.funding.fundingboost.domain.service.pay.FriendPayService;
import kcs.funding.fundingboost.domain.service.pay.MyPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PayController.class)
class PayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendPayService friendPayService;

    @MockBean
    private MyPayService myPayService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Delivery delivery;
    private Funding funding1;
    private Item item;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = MemberFixture.member1();
        delivery = DeliveryFixture.address1(member);
        item = ItemFixture.item1();
        funding1 = FundingFixture.Birthday(member);
        order = OrderFixture.order1(member, delivery);
        orderItem = OrderItemFixture.quantity1(order, item);
    }


    @DisplayName("친구 펀딩 결제 페이지 조회")
    @Test
    void viewFriendsFundingDetail() throws Exception {
        FriendFundingPayingDto friendFundingPayingDto = FriendFundingPayingDto.fromEntity(funding1, member.getPoint());

        given(friendPayService.getFriendFundingPay(funding1.getFundingId(), member.getMemberId())).willReturn(
                friendFundingPayingDto);

        mockMvc.perform(get("/api/v1/pay/friends/{fundingId}", funding1.getFundingId())
                        .param("memberId", member.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.friendName").value("임창희"))
                .andExpect(jsonPath("$.data.friendProfile").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.totalPrice").value(0))
                .andExpect(jsonPath("$.data.presentPrice").value(197000))
                .andExpect(jsonPath("$.data.myPoint").value(46000));
    }

    @DisplayName("친구 펀딩 결제하기")
    @Test
    void fundFriend() throws Exception {
        FriendPayProcessDto friendPayProcessDto = new FriendPayProcessDto(10000, 30000);
        CommonSuccessDto expectedResponse = new CommonSuccessDto(true);

        given(friendPayService.fund(member.getMemberId(), funding1.getFundingId(), friendPayProcessDto)).willReturn(
                expectedResponse);

        String content = objectMapper.writeValueAsString(friendPayProcessDto);

        mockMvc.perform(post("/api/v1/pay/friends/{fundingId}", funding1.getFundingId())
                        .param("memberId", member.getMemberId().toString())
                        .contentType(APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isSuccess").value(true));
    }
}