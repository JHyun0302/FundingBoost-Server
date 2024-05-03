package kcs.funding.fundingboost.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.response.common.CommonFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMyFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeViewDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.repository.fundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class FundingServiceTest_InHo {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FundingRepository fundingRepository;

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private FundingItemRepository fundingItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private FundingService fundingService;

    private Member member;


    @BeforeEach
    void setUp() {
        member = createMember();
    }


    @Test
    void getFundingRegister() {
    }

    @Test
    void putFundingAndFundingItem() {
    }

    @Test
    void terminateFunding() {
    }

    @Test
    void viewFriendsFundingDetail() {
    }

    @Test
    void getFriendFundingList() {
    }

    @Test
    void extendFunding() {
    }

    @DisplayName("메인 페이지 조회: 로그인")
    @Test
    void getMainViewLogin() {
        //given
        /**
         *  사용자 펀딩
         **/
        Optional<Funding> funding = Optional.of(createFunding(member));
        Funding notOptionalFunding = funding.orElseThrow(RuntimeException::new);

        /**
         *  친구
         **/
        Member friend1 = createFriend1();
        Member friend2 = createFriend2();

        /**
         *  사용자와 친구 관계 설정
         **/
        List<Relationship> relationshipList = List.of(createMyRelationship(member, friend1),
                createMyRelationship(member, friend2));

        /**
         * 친구 펀딩
         */
        Funding friendFunding1 = createFunding(friend1);
        Funding friendFunding2 = createFunding(friend2);
        List<Funding> friendFundingList = List.of(friendFunding1, friendFunding2);

        /**
         * 아이템
         */
        Item item1 = createItem1();
        Item item2 = createItem2();
        ReflectionTestUtils.setField(item1, "itemId", 1L);
        ReflectionTestUtils.setField(item2, "itemId", 2L);

        /**
         * 내 펀딩 아이템
         */
        FundingItem myFundingItem1 = FundingItem.createFundingItem(notOptionalFunding, item1, 1);
        FundingItem myFundingItem2 = FundingItem.createFundingItem(notOptionalFunding, item2, 2);

        /**
         * 친구 펀딩 아이템
         */
        FundingItem friendFunding1FundingItem1 = createFundingItem(friendFunding1, item1, 1);
        FundingItem friendFunding1FundingItem2 = createFundingItem(friendFunding1, item2, 2);
        List<FundingItem> friendFunding1FundingItems = List.of(friendFunding1FundingItem1, friendFunding1FundingItem2);

        FundingItem friendFunding2FundingItem1 = createFundingItem(friendFunding2, item1, 1);
        FundingItem friendFunding2FundingItem2 = createFundingItem(friendFunding2, item2, 2);
        List<FundingItem> friendFunding2FundingItems = List.of(friendFunding2FundingItem1, friendFunding2FundingItem2);

        List<List<FundingItem>> friendsFundingsItems = List.of(friendFunding1FundingItems, friendFunding2FundingItems);

        /**
         * 기댓값
         */
        int[] expectedFriendFundingPercent = {77, 77};

        /**
         * 레포지토리 접근
         */
        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);
        for (int i = 0; i < relationshipList.size(); i++) {
            when(fundingRepository.findByMemberIdAndStatus(relationshipList.get(i).getFriend().getMemberId(), true))
                    .thenReturn(Optional.of(friendFundingList.get(i)));
            when(fundingItemRepository.findFundingItemIdListByFunding(friendFundingList.get(i).getFundingId()))
                    .thenReturn(friendsFundingsItems.get(i));
        }
        when(fundingRepository.findFundingInfo(member.getMemberId())).thenReturn(funding);
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.ofNullable(member));
        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        //when
        HomeViewDto result = fundingService.getMainView(member.getMemberId());

        //then
        assertNotNull(result);
        assertThat(result.homeMemberInfoDto()).extracting("nickName").isEqualTo(member.getNickName());
        assertThat(result.homeMyFundingStatusDto()).extracting("deadline")
                .isEqualTo("D-" + ChronoUnit.DAYS.between(LocalDate.now(), funding.get()
                        .getDeadline()));
        assertThat(result.homeMyFundingItemDtoList()).extracting("itemPercent").contains(100, 50);
        assertThat(result.homeFriendFundingDtoList()).extracting("nowFundingItemImageUrl").contains(
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg");
        for (int i = 0; i < result.homeFriendFundingDtoList().size(); i++) {
            assertThat(result.homeFriendFundingDtoList().get(i).commonFriendFundingDto()).extracting(
                    "friendFundingPercent").isEqualTo(expectedFriendFundingPercent[i]);
        }
        assertThat(result.itemDtoList()).extracting("itemId").contains(1L, 2L);
    }

    @DisplayName("메인페이지 조회 - 로그인 안 돼있는 경우")
    @Test
    void getMainViewLogOut() {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty()); // 로그인되지 않은 상태를 시뮬레이션
        //when & then
        assertThatThrownBy(() -> fundingService.getMainView(member.getMemberId()))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining("해당 사용자가 존재하지 않습니다.");
    }

    @DisplayName("메인페이지 조회 - 상품 목록이 없는 경우")
    @Test
    void getMainViewNoItem() {
        //given
        /**
         *  사용자 펀딩
         **/
        Optional<Funding> funding = Optional.of(createFunding(member));
        Funding notOptionalFunding = funding.orElseThrow(RuntimeException::new);

        /**
         *  친구
         **/
        Member friend1 = createFriend1();
        Member friend2 = createFriend2();

        /**
         *  사용자와 친구 관계 설정
         **/
        List<Relationship> relationshipList = List.of(createMyRelationship(member, friend1),
                createMyRelationship(member, friend2));

        /**
         * 친구 펀딩
         */
        Funding friendFunding1 = createFunding(friend1);
        Funding friendFunding2 = createFunding(friend2);
        List<Funding> friendFundingList = List.of(friendFunding1, friendFunding2);

        /**
         * 아이템
         */
        Item item1 = createItem1();
        Item item2 = createItem2();
        ReflectionTestUtils.setField(item1, "itemId", 1L);
        ReflectionTestUtils.setField(item2, "itemId", 2L);

        /**
         * 내 펀딩 아이템
         */
        FundingItem myFundingItem1 = FundingItem.createFundingItem(notOptionalFunding, item1, 1);
        FundingItem myFundingItem2 = FundingItem.createFundingItem(notOptionalFunding, item2, 2);

        /**
         * 친구 펀딩 아이템
         */
        FundingItem friendFunding1FundingItem1 = createFundingItem(friendFunding1, item1, 1);
        FundingItem friendFunding1FundingItem2 = createFundingItem(friendFunding1, item2, 2);
        List<FundingItem> friendFunding1FundingItems = List.of(friendFunding1FundingItem1, friendFunding1FundingItem2);

        FundingItem friendFunding2FundingItem1 = createFundingItem(friendFunding2, item1, 1);
        FundingItem friendFunding2FundingItem2 = createFundingItem(friendFunding2, item2, 2);
        List<FundingItem> friendFunding2FundingItems = List.of(friendFunding2FundingItem1, friendFunding2FundingItem2);

        List<List<FundingItem>> friendsFundingsItems = List.of(friendFunding1FundingItems, friendFunding2FundingItems);

        /**
         * 기댓값
         */
        int[] expectedFriendFundingPercent = {77, 77};

        /**
         * 레포지토리 접근
         */
        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);
        for (int i = 0; i < relationshipList.size(); i++) {
            when(fundingRepository.findByMemberIdAndStatus(relationshipList.get(i).getFriend().getMemberId(), true))
                    .thenReturn(Optional.of(friendFundingList.get(i)));
            when(fundingItemRepository.findFundingItemIdListByFunding(friendFundingList.get(i).getFundingId()))
                    .thenReturn(friendsFundingsItems.get(i));
        }
        when(fundingRepository.findFundingInfo(member.getMemberId())).thenReturn(funding);
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.ofNullable(member));
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        HomeViewDto result = fundingService.getMainView(member.getMemberId());

        //then
        assertNotNull(result);
        assertThat(result.homeMemberInfoDto()).extracting("nickName").isEqualTo(member.getNickName());
        assertThat(result.homeMyFundingStatusDto()).extracting("deadline")
                .isEqualTo("D-" + ChronoUnit.DAYS.between(LocalDate.now(), funding.get()
                        .getDeadline()));
        assertThat(result.homeMyFundingItemDtoList()).extracting("itemPercent").contains(100, 50);
        assertThat(result.homeFriendFundingDtoList()).extracting("nowFundingItemImageUrl").contains(
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg");
        for (int i = 0; i < result.homeFriendFundingDtoList().size(); i++) {
            assertThat(result.homeFriendFundingDtoList().get(i).commonFriendFundingDto()).extracting(
                    "friendFundingPercent").isEqualTo(expectedFriendFundingPercent[i]);
        }
        assertThat(result.itemDtoList()).isEmpty();
    }

    @DisplayName("메인페이지 조회 (getMyFundingStatus)- 펀딩 중 : 사용자 펀딩 상태 조회")
    @Test
    void getMyFundingStatusValidFunding() {
        // Given
        Funding funding = createFunding(member);

        String expectedDeadline =
                "D-" + (int) ChronoUnit.DAYS.between(LocalDate.now(), LocalDateTime.of(2024, 5, 25, 23, 59));

        // When
        HomeMyFundingStatusDto resultDto = ReflectionTestUtils.invokeMethod(fundingService,
                "getMyFundingStatusAndDeadLine",
                funding);

        // Then
        assertNotNull(resultDto);
        assertEquals(expectedDeadline, resultDto.deadline());
        assertEquals(funding.getFundingId(), resultDto.fundingId());
    }

    @DisplayName("메인페이지 조회 (getMyFundingStatus)- 펀딩이 존재 하지 않음")
    @Test
    void getMyFundingStatusInvalidFunding() {
        //given
        Funding funding = null;
        //when
        HomeMyFundingStatusDto resultDto = ReflectionTestUtils.invokeMethod(fundingService,
                "getMyFundingStatusAndDeadLine",
                funding);
        //then
        assertNull(resultDto);
    }

    @DisplayName("메인페이지 조회 (getMyFundingItems)- 펀딩 존재 시: 사용자 펀딩 아이탬 조회")
    @Test
    void getMyFundingItemsValidFunding() {
        //given
        Funding funding = createFunding(member);

        Item item1 = createItem1();
        Item item2 = createItem2();

        FundingItem fundingItem1 = FundingItem.createFundingItem(funding, item1, 1);
        FundingItem fundingItem2 = FundingItem.createFundingItem(funding, item2, 2);

        //when
        List<HomeMyFundingItemDto> result = ReflectionTestUtils.invokeMethod(fundingService, "getMyFundingItems",
                funding);

        //then
        assertNotNull(result);
        assertThat(result).hasSize(2);
        assertThat(result).extracting("itemImageUrl").contains(
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg",
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg");
        assertThat(result).extracting("itemPercent").contains(100, 50);
    }

    @DisplayName("메인페이지 조회 (getMyFundingItems)- 펀딩이 존재 하지 않을 시")
    @Test
    void getMyFundingItemsInvalidFunding() {
        //given
        Funding funding = null;
        //when
        List<HomeMyFundingItemDto> result = ReflectionTestUtils.invokeMethod(fundingService, "getMyFundingItems",
                funding);
        //then
        assertNull(result);
    }

    @DisplayName("메인페이지 조회 ()- 친구 펀딩이 존재 시 : 사용자의 친구 펀딩 상태 조회")
    @Test
    void getFriendFundingListByHomeVaildFreindFunding() {
        //given
        Member friend1 = createFriend1();
        Member friend2 = createFriend2();

        List<Relationship> relationshipList = List.of(createMyRelationship(member, friend1),
                createMyRelationship(member, friend2));

        Funding friendFunding1 = createFunding(friend1);
        Funding friendFunding2 = createFunding(friend2);
        List<Funding> friendFundingList = List.of(friendFunding1, friendFunding2);

        Item item1 = createItem1();
        Item item2 = createItem2();

        FundingItem friendFunding1FundingItem1 = createFundingItem(friendFunding1, item1, 1);
        FundingItem friendFunding1FundingItem2 = createFundingItem(friendFunding1, item2, 2);
        List<FundingItem> friendFunding1FundingItems = List.of(friendFunding1FundingItem1, friendFunding1FundingItem2);

        FundingItem friendFunding2FundingItem1 = createFundingItem(friendFunding2, item1, 1);
        FundingItem friendFunding2FundingItem2 = createFundingItem(friendFunding2, item2, 2);
        List<FundingItem> friendFunding2FundingItems = List.of(friendFunding2FundingItem1, friendFunding2FundingItem2);

        List<List<FundingItem>> friendsFundingsItems = List.of(friendFunding1FundingItems, friendFunding2FundingItems);

        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);
        for (int i = 0; i < relationshipList.size(); i++) {
            when(fundingRepository.findByMemberIdAndStatus(relationshipList.get(i).getFriend().getMemberId(), true))
                    .thenReturn(Optional.of(friendFundingList.get(i)));
            when(fundingItemRepository.findFundingItemIdListByFunding(friendFundingList.get(i).getFundingId()))
                    .thenReturn(friendsFundingsItems.get(i));
        }
        //when
        List<HomeFriendFundingDto> result = ReflectionTestUtils.invokeMethod(fundingService,
                "getFriendFundingListByHome", member.getMemberId());
        //then
        assertNotNull(result);
        assertThat(result).hasSize(2);
        assertThat(result).extracting("nowFundingItemImageUrl").contains(
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg");
    }

    @DisplayName("친구 펀딩이 존재하지 않을 시(commonFriendFundingDtoList 가 존재하지 않을 시)")
    @Test
    void getFriendFundingListByHomeInvaildNotFriendFunding() {
        //given
        // 친구가 없어서 commonFriendFundingDtoList 가 null인 경우로 설정
        List<Relationship> relationshipList = new ArrayList<>();
        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);

        //when
        List<HomeFriendFundingDto> result = ReflectionTestUtils.invokeMethod(fundingService,
                "getFriendFundingListByHome", member.getMemberId());
        //then
        assertThat(result).hasSize(0); // result 값이 Null이 아닌 []로 처리
    }

    @DisplayName("메인페이지 조회 - (getCommonFriendFundingList) 친구 펀딩 존재 시")
    @Test
    void geCommonFriendFundingListValid() {
        //given
        Member friend1 = createFriend1();
        Member friend2 = createFriend2();

        List<Relationship> relationshipList = List.of(createMyRelationship(member, friend1),
                createMyRelationship(member, friend2));

        Funding friendFunding1 = createFunding(friend1);
        Funding friendFunding2 = createFunding(friend2);
        List<Funding> friendFundingList = List.of(friendFunding1, friendFunding2);

        Item item1 = createItem1();
        Item item2 = createItem2();

        FundingItem friendFunding1FundingItem1 = createFundingItem(friendFunding1, item1, 1);
        FundingItem friendFunding1FundingItem2 = createFundingItem(friendFunding1, item2, 2);
        List<FundingItem> friendFunding1FundingItems = List.of(friendFunding1FundingItem1, friendFunding1FundingItem2);

        FundingItem friendFunding2FundingItem1 = createFundingItem(friendFunding2, item1, 1);
        FundingItem friendFunding2FundingItem2 = createFundingItem(friendFunding2, item2, 2);
        List<FundingItem> friendFunding2FundingItems = List.of(friendFunding2FundingItem1, friendFunding2FundingItem2);

        List<List<FundingItem>> friendsFundingsItems = List.of(friendFunding1FundingItems, friendFunding2FundingItems);

        String expectedDeadline =
                "D-" + (int) ChronoUnit.DAYS.between(LocalDate.now(), LocalDateTime.of(2024, 5, 25, 23, 59));

        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);
        for (int i = 0; i < relationshipList.size(); i++) {
            when(fundingRepository.findByMemberIdAndStatus(relationshipList.get(i).getFriend().getMemberId(), true))
                    .thenReturn(Optional.of(friendFundingList.get(i)));
            when(fundingItemRepository.findFundingItemIdListByFunding(friendFundingList.get(i).getFundingId()))
                    .thenReturn(friendsFundingsItems.get(i));
        }
        //when
        List<CommonFriendFundingDto> result = ReflectionTestUtils.invokeMethod(fundingService,
                "getCommonFriendFundingList", member.getMemberId());
        //then
        assertNotNull(result);
        assertThat(result).hasSize(2);
        assertThat(result).extracting("nickName").contains("맹인호", "구태형");
        assertThat(result).extracting("tag").contains("#기타");
        assertThat(result).extracting("friendFundingPercent").contains(77);
        assertThat(result).extracting("friendFundingDeadlineDate").contains(expectedDeadline);
    }

    @DisplayName("메인페이지 조회 - (getCommonFriendFundingList) 친구가 없을 시")
    @Test
    void geCommonFriendFundingListInValidNotFriend() {
        // given
        List<Relationship> relationshipList = new ArrayList<>();
        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);

        // when
        List<CommonFriendFundingDto> result = ReflectionTestUtils.invokeMethod(fundingService,
                "getCommonFriendFundingList", member.getMemberId());

        // then
        assertThat(result).hasSize(0);
    }

    @DisplayName("메인페이지 조회 - (getCommonFriendFundingList) 진행중인 펀딩이 없을 때")
    @Test
    void getCommonFriendFundingListInvalidNotFunding() {
        // given
        Member friend1 = createFriend1();
        Member friend2 = createFriend2();

        List<Relationship> relationshipList = List.of(createMyRelationship(member, friend1),
                createMyRelationship(member, friend2));

        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);
        for (Relationship relationship : relationshipList) {
            when(fundingRepository.findByMemberIdAndStatus(relationship.getFriend().getMemberId(), true))
                    .thenReturn(Optional.empty());
        }
        // when
        List<CommonFriendFundingDto> result = ReflectionTestUtils.invokeMethod(fundingService,
                "getCommonFriendFundingList", member.getMemberId());
        // then
        assertThat(result).hasSize(0);
    }

    @Test
    void getMyFundingHistory() {
    }

    @Test
    void getMyFundingHistoryDetails() {
    }

    @Test
    void getFriendFundingHistory() {
    }

    private static Member createMember() {
        Member member = Member.createMember("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");

        ReflectionTestUtils.setField(member, "memberId", 1L);

        return member;
    }

    private static Member createFriend1() {
        Member friend = Member.createMember("맹인호", "aoddlsgh98@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/woBG0lIJfU/M6aVERkQ2Lv2sNfQaLMYrK/pzfmfl_110x110_c.jpg",
                "", "aFluW29Ya1hpRXdBdEdyQHBGdlprW25baFFmDQ");

        ReflectionTestUtils.setField(friend, "memberId", 2L);

        return friend;
    }

    private static Member createFriend2() {
        Member friend = Member.createMember("구태형", "rnxogud136@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg",
                "", "aFtpX2lZaFhvQ3JLe0J2QnFDcFxtXWhdbldgDA");

        ReflectionTestUtils.setField(friend, "memberId", 3L);

        return friend;
    }


    private static Funding createFunding(Member member) {

        Funding funding = Funding.createFundingForTest(member, "아 하기 싫다", Tag.ETC, 112000, 86500,
                LocalDateTime.of(2024, 5, 25, 23, 59));

        ReflectionTestUtils.setField(funding, "fundingStatus", true);

        return funding;
    }

    private static Item createItem1() {
        return Item.createItem("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션", 61000,
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg",
                "샤넬", "뷰티", "00:00");
    }

    private static Item createItem2() {
        return Item.createItem("NEW 루쥬 코코 밤(+샤넬 기프트 카드)", 51000,
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg",
                "샤넬", "뷰티", "934 코랄린 [NEW]");
    }

    private static Relationship createMyRelationship(Member member, Member friend) {
        return Relationship.createRelationships(member, friend).get(0);
    }

    private static FundingItem createFundingItem(Funding funding, Item item, int sequence) {
        return FundingItem.createFundingItem(funding, item, sequence);
    }


}