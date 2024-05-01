package kcs.funding.fundingboost.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.response.myPage.orderHistory.OrderHistoryDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.entity.OrderItem;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.orderItem.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember();
    }

    @DisplayName("주문 목록이 존재하는 경우")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션, 61000, https://img1.kakaocdn.net/..., 샤넬, 뷰티, 00:00",
            "NEW 루쥬 코코 밤(+샤넬 기프트 카드), 51000, https://img1.kakaocdn.net/..., 샤넬, 뷰티, 934 코랄린 [NEW]"
    })
    void getOrderHistory_WhenOrderListExists(String itemName, int itemPrice, String itemImageUrl, String brandName,
                                             String category, String optionName) {
        //given
        Item item = Item.createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
        Delivery delivery = Delivery.createDelivery("경기도 성남시 분당구 판교역로 166", "010-1234-5678", "사무실", member);
        Order order = Order.createOrder(itemPrice, member, delivery);
        OrderItem orderItem = OrderItem.createOrderItem(order, item, 1);

        when(orderItemRepository.findLastOrderByMemberId(member.getMemberId())).thenReturn(
                List.of(orderItem));
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));

        //when
        OrderHistoryDto result = orderService.getOrderHistory(member.getMemberId());

        //then
        assertNotNull(result);
        assertThat(result.orderHistoryItemDtoList()).hasSize(1).extracting("itemName").contains(itemName);
        verify(orderItemRepository, times(1)).findLastOrderByMemberId(member.getMemberId());
    }


    @DisplayName("주문 목록이 존재하지 경우")
    @Test
    void getOrderHistory_WhenOrderListDoesNotExist() {
        //given
        when(orderItemRepository.findLastOrderByMemberId(member.getMemberId())).thenReturn(Collections.emptyList());
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));

        //when
        OrderHistoryDto result = orderService.getOrderHistory(member.getMemberId());

        //then
        assertNotNull(result);
        assertThat(result.orderHistoryItemDtoList()).isNull();
        verify(memberRepository, times(1)).findById(member.getMemberId());
    }

    private static Member createMember() {
        return Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }
}