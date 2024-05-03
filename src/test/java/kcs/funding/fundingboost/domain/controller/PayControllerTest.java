package kcs.funding.fundingboost.domain.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.pay.myPay.ItemPayDto;
import kcs.funding.fundingboost.domain.dto.request.pay.myPay.MyPayDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.DeliveryDto;
import kcs.funding.fundingboost.domain.dto.response.pay.myPay.MyFundingPayViewDto;
import kcs.funding.fundingboost.domain.dto.response.pay.myPay.MyOrderPayViewDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.model.DeliveryFixture;
import kcs.funding.fundingboost.domain.model.FundingFixture;
import kcs.funding.fundingboost.domain.model.FundingItemFixture;
import kcs.funding.fundingboost.domain.model.GiftHubItemFixture;
import kcs.funding.fundingboost.domain.model.ItemFixture;
import kcs.funding.fundingboost.domain.model.MemberFixture;
import kcs.funding.fundingboost.domain.service.pay.FriendPayService;
import kcs.funding.fundingboost.domain.service.pay.MyPayService;
import org.junit.jupiter.api.BeforeEach;
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
    private MyPayService myPayService;

    @MockBean
    private FriendPayService friendPayService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private FundingItem fundingItem;
    private Delivery delivery;
    private Funding funding;
    private Item item;
    private GiftHubItem giftHubItem;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = MemberFixture.member1();
        delivery = DeliveryFixture.address1(member);
        item = ItemFixture.item1();
        giftHubItem = GiftHubItemFixture.quantity1(item, member);
    }

    @Test
    void myOrderPayView() throws Exception {
        List<DeliveryDto> deliveryDtoList = List.of(DeliveryDto.fromEntity(delivery));

        MyOrderPayViewDto myOrderPayViewDto = MyOrderPayViewDto.fromEntity(deliveryDtoList, member.getPoint());

        given(myPayService.myOrderPayView(member.getMemberId())).willReturn(myOrderPayViewDto);

        mockMvc.perform(get("/api/v1/pay/view/order")
                        .param("memberId", member.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.deliveryListDto[0].deliveryId").value(delivery.getDeliveryId()))
                .andExpect(jsonPath("$.data.deliveryListDto[0].customerName").value(delivery.getCustomerName()))
                .andExpect(jsonPath("$.data.deliveryListDto[0].address").value(delivery.getAddress()))
                .andExpect(jsonPath("$.data.deliveryListDto[0].phoneNumber").value(delivery.getPhoneNumber()))
                .andExpect(jsonPath("$.data.point").value(member.getPoint()));
    }

    @Test
    void myFundingPayView() throws Exception {
        funding = FundingFixture.terminatedFundingSuccess(member, 10000);

        fundingItem = FundingItemFixture.fundingItem2(item, funding);

        List<DeliveryDto> deliveryDtoList = List.of(DeliveryDto.fromEntity(delivery));

        MyFundingPayViewDto myFundingPayViewDto = MyFundingPayViewDto.fromEntity(funding, deliveryDtoList);

        given(myPayService.myFundingPayView(fundingItem.getFundingItemId(), member.getMemberId())).willReturn(
                myFundingPayViewDto);

        mockMvc.perform(get("/api/v1/pay/view/funding/2")
                        .param("memberId", member.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.deliveryDtoList[0].deliveryId").value(delivery.getDeliveryId()))
                .andExpect(jsonPath("$.data.deliveryDtoList[0].customerName").value(delivery.getCustomerName()))
                .andExpect(jsonPath("$.data.deliveryDtoList[0].address").value(delivery.getAddress()))
                .andExpect(jsonPath("$.data.deliveryDtoList[0].phoneNumber").value(delivery.getPhoneNumber()))
                .andExpect(jsonPath("$.data.point").value(member.getPoint()))
                .andExpect(jsonPath("$.data.collectPrice").value(funding.getCollectPrice()));
    }

    @Test
    void payMyOrder() throws Exception {
        ItemPayDto itemPayDto = new ItemPayDto(item.getItemId(), giftHubItem.getGiftHubItemId(), 2);
        List<ItemPayDto> itemPayDtoList = List.of(itemPayDto);
        MyPayDto myPayDto = new MyPayDto(itemPayDtoList, delivery.getDeliveryId(), 1000);
        CommonSuccessDto expectedResponse = new CommonSuccessDto(true);

        given(myPayService.payMyItem(myPayDto, member.getMemberId())).willReturn(expectedResponse);

        String content = objectMapper.writeValueAsString(myPayDto);

        mockMvc.perform(post("/api/v1/pay/order")
                        .param("memberId", member.getMemberId().toString())
                        .contentType(APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.isSuccess").value(true));
    }
}