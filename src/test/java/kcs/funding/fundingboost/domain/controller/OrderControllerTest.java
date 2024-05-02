package kcs.funding.fundingboost.domain.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory.OrderHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory.OrderHistoryItemDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.entity.OrderItem;
import kcs.funding.fundingboost.domain.service.OrderService;
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
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Delivery delivery;
    private Item item1;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = createMember();
        delivery = Delivery.createDelivery("서울시 강남구 역삼동", "010-1234-5678", "임창희", member);
        item1 = createItem();
        Order order = createOrder();
        orderItem = OrderItem.createOrderItem(order, item1, 1);
    }

    @DisplayName("마이페이지-지난 주문 내역 조회(주문 내역 O)")
    @Test
    void orderHistory() throws Exception {
        List<OrderItem> orderItemList = List.of(orderItem);
        List<OrderHistoryItemDto> orderHistoryItemDtoList = orderItemList.stream()
                .map(OrderHistoryItemDto::fromEntity)
                .toList();
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);

        given(orderService.getOrderHistory(member.getMemberId()))
                .willReturn(OrderHistoryDto.fromEntity(myPageMemberDto, orderHistoryItemDtoList));

        mockMvc.perform(get("/api/v1/order/history")
                        .param("memberId", String.valueOf(member.getMemberId()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.myPageMemberDto.nickname").value("임창희"))
                .andExpect(jsonPath("$.data.myPageMemberDto.email").value("dlackdgml3710@gmail.com"))
                .andExpect(jsonPath("$.data.myPageMemberDto.profileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.orderHistoryItemDtoList[0].itemName").value("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션"))
                .andExpect(jsonPath("$.data.orderHistoryItemDtoList[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data.orderHistoryItemDtoList[0].optionName").value("00:00"))
                .andExpect(jsonPath("$.data.orderHistoryItemDtoList[0].quantity").value(1))
                .andExpect(jsonPath("$.data.orderHistoryItemDtoList[0].price").value(61000));
    }

    @DisplayName("마이페이지-지난 주문 내역 조회(주문 내역 x)")
    @Test
    void orderHistory_NotFoundOrder() throws Exception {
        List<OrderItem> orderItemList = List.of();

        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);

        given(orderService.getOrderHistory(member.getMemberId()))
                .willReturn(OrderHistoryDto.fromEntity(myPageMemberDto, null));

        mockMvc.perform(get("/api/v1/order/history")
                        .param("memberId", String.valueOf(member.getMemberId()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.myPageMemberDto.nickname").value("임창희"))
                .andExpect(jsonPath("$.data.myPageMemberDto.email").value("dlackdgml3710@gmail.com"))
                .andExpect(jsonPath("$.data.myPageMemberDto.profileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.orderHistoryItemDtoList").isEmpty());
    }

    private static Member createMember() throws NoSuchFieldException, IllegalAccessException {
        Member member = Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
        Field memberId = member.getClass().getDeclaredField("memberId");
        memberId.setAccessible(true);
        memberId.set(member, 1L);
        return member;
    }

    private Order createOrder() throws NoSuchFieldException, IllegalAccessException {
        Order order = Order.createOrder(61000, member, delivery);
        Field orderId = order.getClass().getDeclaredField("orderId");
        orderId.setAccessible(true);
        orderId.set(order, 1L);
        return order;
    }

    private static Item createItem() {
        return Item.createItem("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션", 61000,
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg",
                "샤넬", "뷰티", "00:00");
    }
}