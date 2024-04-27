package kcs.funding.fundingboost.domain.service.pay;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_FUNDING_MONEY;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_FUNDING;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.FriendPayProcessDto;
import kcs.funding.fundingboost.domain.dto.response.FriendFundingPayingDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.service.utils.PayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FriendPayServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FundingRepository fundingRepository;

    @InjectMocks
    private FriendPayService friendPayService;

    private Member member;

    private Funding funding;

    @BeforeEach
    void setup() {
        member = createMember();
        funding = createFunding(member);
    }


    @DisplayName("친구 펀딩 결제 조회 성공")
    @Test
    public void getFriendFundingPay_Success() {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(fundingRepository.findById(anyLong())).thenReturn(Optional.of(funding));

        //when
        FriendFundingPayingDto resultDto = friendPayService.getFriendFundingPay(1L, 1L);

        //then
        assertThat(funding.getTotalPrice()).isEqualTo(resultDto.totalPrice());
        assertThat(member.getPoint()).isEqualTo(resultDto.myPoint());
    }

    @DisplayName("친구 펀딩 결제 조회 실패 : Member Not Found")
    @Test
    public void getFriendFundingPay_MemberNotFound_ThrowsException() {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        CommonException exception = assertThrows(CommonException.class,
                () -> friendPayService.getFriendFundingPay(1L, 1L));

        //then
        assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }

    @DisplayName("친구 펀딩 결제 성공")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @ValueSource(ints = {1000, 2000, 3000})
    void fund_Success(int myPoint) {
        //given
        FriendPayProcessDto friendPayProcessDto = new FriendPayProcessDto(myPoint);

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(fundingRepository.findById(funding.getFundingId())).thenReturn(Optional.of(funding));

        //when & then
        // Mockito-inline : 정적 메서드인 deductPointsIfPossible 메서드를 목킹하는 방법
        try (MockedStatic<PayUtils> mocked = Mockito.mockStatic(PayUtils.class)) { // MockedStatic<PayUtils> 객체 생성
            mocked.when(() -> PayUtils.deductPointsIfPossible(any(Member.class), anyInt()))
                    .thenAnswer(invocation -> null);

            CommonSuccessDto result = friendPayService.fund(member.getMemberId(), funding.getFundingId(),
                    friendPayProcessDto);

            assertTrue(result.isSuccess());
            verify(memberRepository).findById(member.getMemberId());
            verify(fundingRepository).findById(funding.getFundingId());
        }
    }

    @DisplayName("친구 펀딩 결제 실패 : Member Not Found")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @ValueSource(ints = {1000, 2000, 3000})
    void fund_MemberNotFound_ThrowsException(int myPoint) {
        //given
        FriendPayProcessDto dto = new FriendPayProcessDto(myPoint);

        when(memberRepository.findById(member.getMemberId())).thenThrow(new CommonException(NOT_FOUND_MEMBER));

        //when
        CommonException exception = assertThrows(CommonException.class,
                () -> friendPayService.fund(member.getMemberId(), funding.getFundingId(), dto));

        //then
        assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }


    @DisplayName("친구 펀딩 결제 실패 : Member Not Found")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @ValueSource(ints = {1000, 2000, 3000})
    void fund_FundingNotFound_ThrowsException(int myPoint) {
        //given
        FriendPayProcessDto dto = new FriendPayProcessDto(myPoint);

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(fundingRepository.findById(funding.getFundingId())).thenThrow(new CommonException(NOT_FOUND_FUNDING));

        //when
        CommonException exception = assertThrows(CommonException.class,
                () -> friendPayService.fund(member.getMemberId(), funding.getFundingId(), dto));

        //then
        assertEquals(NOT_FOUND_FUNDING.getMessage(), exception.getMessage());
    }

    @DisplayName("친구 펀딩 결제 실패 : Invalid Funding Money")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @ValueSource(ints = {10001, 20000, 30000})
    void fund_InvalidFundingMoney_ThrowsException(int myPoint) {
        //given
        FriendPayProcessDto dto = new FriendPayProcessDto(myPoint);

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(fundingRepository.findById(funding.getFundingId())).thenReturn(Optional.of(funding));

        //when
        CommonException exception = assertThrows(CommonException.class,
                () -> friendPayService.fund(member.getMemberId(), funding.getFundingId(), dto));

        //then
        assertEquals(INVALID_FUNDING_MONEY.getMessage(), exception.getMessage());
    }

    private static Member createMember() {
        return Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }

    private static Funding createFunding(Member member) {
        return Funding.createFundingForTest(member, "생일축하해줘", Tag.BIRTHDAY, 100000, 90000,
                LocalDateTime.now().plusDays(14));
    }
}