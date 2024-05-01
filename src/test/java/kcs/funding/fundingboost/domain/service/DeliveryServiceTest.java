package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.response.myPage.deliveryManage.MyPageDeliveryManageDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.DeliveryRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DeliveryServiceTest {
    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    private Member member;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = createMember();
        Field memberId = member.getClass().getDeclaredField("memberId");
        memberId.setAccessible(true);
        memberId.set(member, 1L);
    }

    @DisplayName("마이페이지 - 배송지 조회 성공")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'서울특별시 강남구 테헤란로', '010-1234-1234', '홍길동'",
            "'경기도 수원시 행궁동', '010-1234-1234', '마동석'"
    })
    void getMyDeliveryManageList(String address, String phoneNumber, String customerName) {
        //given
        Delivery delivery = createDelivery(address, phoneNumber, customerName, member);
        List<Delivery> deliveryList = Collections.singletonList(delivery);

        when(deliveryRepository.findAllByMemberId(anyLong())).thenReturn(deliveryList);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        MyPageDeliveryManageDto myPageDeliveryManageDto = deliveryService.getMyDeliveryManageList(member.getMemberId());

        //then
        assertEquals(deliveryList.get(0).getAddress(),
                myPageDeliveryManageDto.myPageDeliveryDtoList().get(0).address());
    }

    @DisplayName("마이페이지 - 배송지 조회 성공 - 배송지 없음")
    @Test
    void getMyDeliveryManageList_DeliveryNotFound() {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        //when
        MyPageDeliveryManageDto myPageDeliveryManageDto = deliveryService.getMyDeliveryManageList(member.getMemberId());
        //then
        assertEquals(List.of(), myPageDeliveryManageDto.myPageDeliveryDtoList());
    }

    @DisplayName("마이페이지 - 배송지 조회 실패 - 사용자를 찾을 수 없음")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'서울특별시 강남구 테헤란로', '010-1234-1234', '홍길동'",
            "'경기도 수원시 행궁동', '010-1234-1234', '마동석'"
    })
    void getMyDeliveryManageList_MemberNotFound(String address, String phoneNumber, String customerName) {
        //given
        Delivery delivery = createDelivery(address, phoneNumber, customerName, member);
        List<Delivery> deliveryList = Collections.singletonList(delivery);

        when(deliveryRepository.findAllByMemberId(anyLong())).thenReturn(deliveryList);
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.ofNullable(member));

        //when
        CommonException exception = assertThrows(CommonException.class, () -> {
            deliveryService.getMyDeliveryManageList(anyLong());
        }); // 사용자를 찾지 못했으므로 실패해야함
        //then
        assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }


    private static Member createMember() {
        return Member.createMember("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }

    private static Delivery createDelivery(String address, String phoneNumber, String customerName, Member member) {
        return Delivery.createDelivery(address, phoneNumber, customerName, member);
    }
}