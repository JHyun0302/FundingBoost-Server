package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_FUNDING;
import static kcs.funding.fundingboost.domain.service.utils.FundingConst.EXTEND_DEADLINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.contributor.ContributorRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.repository.fundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FundingServiceTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    FundingRepository fundingRepository;
    @Mock
    FundingItemRepository fundingItemRepository;
    @Mock
    ContributorRepository contributorRepository;
    @Mock
    RelationshipRepository relationshipRepository;

    @InjectMocks
    FundingService fundingService;

    @Test
    void viewFriendsFundingDetail() {
    }

    @DisplayName("extendFunding 성공 : 아이템이 존재한다면 펀딩 기간이 FundingConst.EXTEND_DEADLINE만큼 증가해야 한다")
    @Test
    void extendFunding_펀딩연장성공() throws NoSuchFieldException, IllegalAccessException {
        // given
        Long fundingId = 1L;
        Funding mockFunding = mock(Funding.class);

        when(fundingRepository.findById(fundingId)).thenReturn(Optional.of(mockFunding));

        // when
        CommonSuccessDto result = fundingService.extendFunding(fundingId);

        // then
        verify(fundingRepository).findById(fundingId); // fundingRepository에서 fundingId로 조회가 발생해야 한다
        verify(mockFunding).extendDeadline(EXTEND_DEADLINE); // funding의 extendDeadline에 EXTEND_DEADLINE인자가 전달돼야 한다
        assertTrue(result.isSuccess()); // 반환 결과는 true여야 한다
    }

    @DisplayName("extendFunding 실패 : 펀딩이 존재하지 않으면 NOT_FOUND_FUNDING 예외가 발생해야 한다")
    @Test
    public void extendFunding_펀딩조회실패() throws Exception {
        // given
        Long fundingId = 1L;

        when(fundingRepository.findById(fundingId)).thenReturn(Optional.empty());
        // when & then
        CommonException exception = assertThrows(CommonException.class,
                () -> fundingService.extendFunding(fundingId)); // 펀딩 id가 존재하지 않으면 예외가 발생해야 한다
        assertEquals(NOT_FOUND_FUNDING.getMessage(), exception.getMessage()); // NOT_FOUND_FUNDING 예외가 발생해야 한다
    }

    @Test
    void getFriendFundingHistory() {
    }
}