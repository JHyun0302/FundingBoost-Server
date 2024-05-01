package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_FUNDING_STATUS;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_FUNDING;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_ITEM;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.fundingRegist.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.friendFunding.FriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingHistory.MyFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingHistory.MyPageFundingDetailHistoryDto;
import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.entity.common.BaseTimeEntity;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.contributor.ContributorRepository;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import kcs.funding.fundingboost.domain.repository.fundingItem.FundingItemRepository;
import kcs.funding.fundingboost.domain.repository.relationship.RelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FundingServiceTest {

    @InjectMocks
    private FundingService fundingService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FundingRepository fundingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FundingItemRepository fundingItemRepository;

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private ContributorRepository contributorRepository;

    private Member member;
    private Item item1;
    private Item item2;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = createMember();
        Field memberId = member.getClass().getDeclaredField("memberId");
        memberId.setAccessible(true);
        memberId.set(member, 1L);

        item1 = createItemId(
                1L,
                "NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션",
                61000,
                "https://img1.kakaocdn.net/...",
                "샤넬",
                "뷰티",
                "00:00");
        item2 = createItemId(
                2L,
                "NEW 루쥬 코코 밤(+샤넬 기프트 카드)",
                51000,
                "https://img1.kakaocdn.net/...",
                "샤넬",
                "뷰티",
                "934 코랄린 [NEW]");
    }

    @DisplayName("펀딩 등록하기")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({"'1,2', '생일 선물 주세요', '생일', '2024-05-15'"})
    void putFundingAndFundingItem_Success(String itemIdListString, String fundingMessage, String tag,
                                          String deadlineString) {
        //given
        List<Long> itemIdList = Arrays.stream(itemIdListString.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        LocalDate deadline = LocalDate.parse(deadlineString);
        RegisterFundingDto registerFundingDto = new RegisterFundingDto(itemIdList, fundingMessage, tag, deadline);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        //when
        CommonSuccessDto commonSuccessDto = fundingService.putFundingAndFundingItem(member.getMemberId(),
                registerFundingDto);

        //then
        assertNotNull(commonSuccessDto);
        assertTrue(commonSuccessDto.isSuccess());
        verify(fundingRepository, times(1)).save(any());
        verify(fundingItemRepository, times(itemIdList.size())).save(any());
    }


    @DisplayName("펀딩 등록하기-실패(사용자를 찾을 수 없음)")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({"'1,2', '생일 선물 주세요', '생일', '2024-05-15'"})
    void putFundingAndFundingItem_NotFoundMember(String itemIdListString, String fundingMessage, String tag,
                                                 String deadlineString) {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
        List<Long> itemIdList = Arrays.stream(itemIdListString.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        LocalDate deadline = LocalDate.parse(deadlineString);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        RegisterFundingDto registerFundingDto = new RegisterFundingDto(itemIdList, fundingMessage, tag, deadline);

        //when
        CommonException exception = assertThrows(CommonException.class, () -> {
            fundingService.putFundingAndFundingItem(member.getMemberId(), registerFundingDto);
        });
        //then
        assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }

    @DisplayName("펀딩 등록하기-실패(아이템을 찾을 수 없음)")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({"'1,2', '생일 선물 주세요', '생일', '2024-05-15'"})
    void putFundingAndFundingItem_NotFoundItem(String itemIdListString, String fundingMessage, String tag,
                                               String deadlineString) {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.ofNullable(member));
        List<Long> itemIdList = Arrays.stream(itemIdListString.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        LocalDate deadline = LocalDate.parse(deadlineString);

        RegisterFundingDto registerFundingDto = new RegisterFundingDto(itemIdList, fundingMessage, tag, deadline);

        //when
        CommonException exception = assertThrows(CommonException.class, () -> {
            fundingService.putFundingAndFundingItem(member.getMemberId(), registerFundingDto);
        });
        //then
        assertEquals(NOT_FOUND_ITEM.getMessage(), exception.getMessage());
    }

    @DisplayName("펀딩 종료하기-성공")
    @Test
    void terminateFunding_Success() throws NoSuchFieldException, IllegalAccessException {
        //given
        Funding funding = Funding.createFunding(
                member, "생일 축하해줘", Tag.BIRTHDAY, 100000,
                LocalDateTime.of(2024, 5, 14, 23, 59));
        Field fundingId = funding.getClass().getDeclaredField("fundingId");
        fundingId.setAccessible(true);
        fundingId.set(funding, 1L);
        when(fundingRepository.findById(1L)).thenReturn(Optional.of(funding));
        //when
        CommonSuccessDto commonSuccessDto = fundingService.terminateFunding(funding.getFundingId());
        //then
        assertNotNull(commonSuccessDto);
        assertTrue(commonSuccessDto.isSuccess());
        verify(fundingRepository, times(1)).findById(any());
        assertFalse(funding.isFundingStatus());
    }

    @DisplayName("펀딩 종료하기-실패(펀딩이 존재하지 않음)")
    @Test
    void terminateFunding_NotFoundFunding() throws NoSuchFieldException, IllegalAccessException {
        //given
        Funding funding = Funding.createFunding(
                member, "생일 축하해줘", Tag.BIRTHDAY, 100000,
                LocalDateTime.of(2024, 5, 14, 23, 59));
        Field fundingId = funding.getClass().getDeclaredField("fundingId");
        fundingId.setAccessible(true);
        fundingId.set(funding, 1L);
        when(fundingRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        CommonException exception = assertThrows(CommonException.class, () -> {
            fundingService.terminateFunding(funding.getFundingId());
        });
        //then
        assertEquals(NOT_FOUND_FUNDING.getMessage(), exception.getMessage());
    }

    @DisplayName("친구 펀딩 목록 조회-성공")
    @Test
    void getFriendFundingList_Success() throws NoSuchFieldException, IllegalAccessException {
        //given
        Member friend = Member.createMember("구태형", "rnxogud136@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg",
                "", "aFtpX2lZaFhvQ3JLe0J2QnFDcFxtXWhdbldgDA");
        Field friendMemberId = friend.getClass().getDeclaredField("memberId");
        friendMemberId.setAccessible(true);
        friendMemberId.set(friend, 2L);

        List<Relationship> myRelationships = getMyRelationships(friend);

        Funding friendFunding = Funding.createFunding(
                friend, "졸업했어유", Tag.GRADUATE, 112000,
                LocalDateTime.of(2024, 5, 14, 23, 59));
        Field friendFundingId = friendFunding.getClass().getDeclaredField("fundingId");
        friendFundingId.setAccessible(true);
        friendFundingId.set(friendFunding, 2L);

        FundingItem fundingItem1 = FundingItem.createFundingItem(friendFunding, item1, 1);
        FundingItem fundingItem2 = FundingItem.createFundingItem(friendFunding, item2, 2);
        List<FundingItem> fundingItems = List.of(fundingItem1, fundingItem2);

        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(myRelationships);
        when(fundingRepository.findByMemberIdAndStatus(friend.getMemberId(), true)).thenReturn(
                Optional.of(friendFunding));
        when(fundingItemRepository.findFundingItemIdListByFunding(friendFunding.getFundingId())).thenReturn(
                fundingItems);
        //when
        List<FriendFundingDto> friendFundingDtoList = fundingService.getFriendFundingList(member.getMemberId());
        //then
        assertNotNull(friendFundingDtoList);
        assertEquals(2L, friendFundingDtoList.get(0).commonFriendFundingDto().fundingId());
        assertEquals("구태형", friendFundingDtoList.get(0).commonFriendFundingDto().nickName());
        assertEquals(61000,
                friendFundingDtoList.get(0).commonFriendFundingDto().friendFundingPageItemDtoList().get(0).itemPrice());
    }

    @DisplayName("친구 펀딩 목록 조회-성공(친구가 없는 경우)")
    @Test
    void getFriendFundingList_NotFoundFriend() {
        //given
        List<Relationship> relationshipList = new ArrayList<>();
        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(relationshipList);

        //when
        List<FriendFundingDto> friendFundingDtoList = fundingService.getFriendFundingList(member.getMemberId());

        //then
        assertEquals(List.of(), friendFundingDtoList);
    }

    @DisplayName("친구 펀딩 목록 조회-성공(친구의 펀딩이 없는 경우)")
    @Test
    void getFriendFundingList_NotFoundFriendFunding() throws NoSuchFieldException, IllegalAccessException {
        //given
        Member friend = Member.createMember("구태형", "rnxogud136@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg",
                "", "aFtpX2lZaFhvQ3JLe0J2QnFDcFxtXWhdbldgDA");
        Field friendMemberId = friend.getClass().getDeclaredField("memberId");
        friendMemberId.setAccessible(true);
        friendMemberId.set(friend, 2L);

        List<Relationship> myRelationships = getMyRelationships(friend);

        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(myRelationships);
        when(fundingRepository.findByMemberIdAndStatus(friend.getMemberId(), true)).thenReturn(
                Optional.empty());
        //when
        List<FriendFundingDto> friendFundingDtoList = fundingService.getFriendFundingList(member.getMemberId());

        //then
        assertEquals(List.of(), friendFundingDtoList);
    }

    @DisplayName("친구 펀딩 목록 조회-실패(친구의 펀딩 총 금액이 0)")
    @Test
    void getFriendFundingList_FriendFundingTotalPriceZero() throws NoSuchFieldException, IllegalAccessException {
        //given
        Member friend = Member.createMember("구태형", "rnxogud136@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg",
                "", "aFtpX2lZaFhvQ3JLe0J2QnFDcFxtXWhdbldgDA");
        Field friendMemberId = friend.getClass().getDeclaredField("memberId");
        friendMemberId.setAccessible(true);
        friendMemberId.set(friend, 2L);

        List<Relationship> myRelationships = getMyRelationships(friend);

        Funding friendFunding = Funding.createFunding(
                friend, "졸업했어유", Tag.GRADUATE, 0,
                LocalDateTime.of(2024, 5, 14, 23, 59));
        Field friendFundingId = friendFunding.getClass().getDeclaredField("fundingId");
        friendFundingId.setAccessible(true);
        friendFundingId.set(friendFunding, 2L);

        List<FundingItem> fundingItems = List.of();

        when(relationshipRepository.findFriendByMemberId(member.getMemberId())).thenReturn(myRelationships);
        when(fundingRepository.findByMemberIdAndStatus(friend.getMemberId(), true)).thenReturn(
                Optional.of(friendFunding));
        when(fundingItemRepository.findFundingItemIdListByFunding(friendFunding.getFundingId())).thenReturn(
                fundingItems);
        //when
        CommonException exception = assertThrows(CommonException.class, () -> {
            fundingService.getFriendFundingList(member.getMemberId());
        });

        //then
        assertEquals(INVALID_FUNDING_STATUS.getMessage(), exception.getMessage());
    }

    @DisplayName("내 펀딩 이력 조회-성공")
    @Test
    void getMyFundingHistory_Success() throws NoSuchFieldException, IllegalAccessException {
        //given
        Funding funding = Funding.createFunding(
                member, "생일 축하해줘", Tag.BIRTHDAY, 112000,
                LocalDateTime.of(2024, 5, 14, 23, 59));
        Field fundingId1 = funding.getClass().getDeclaredField("fundingId");
        fundingId1.setAccessible(true);
        fundingId1.set(funding, 1L);
        Field fundingStatus = funding.getClass().getDeclaredField("fundingStatus");
        fundingStatus.setAccessible(true);
        fundingStatus.set(funding, false);
        Field createdDateField = BaseTimeEntity.class.getDeclaredField("createdDate");
        createdDateField.setAccessible(true);
        createdDateField.set(funding, LocalDateTime.now());

        Member friend = Member.createMember("구태형", "rnxogud136@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg",
                "", "aFtpX2lZaFhvQ3JLe0J2QnFDcFxtXWhdbldgDA");
        Field friendMemberId = friend.getClass().getDeclaredField("memberId");
        friendMemberId.setAccessible(true);
        friendMemberId.set(friend, 2L);

        FundingItem fundingItem1 = FundingItem.createFundingItem(funding, item1, 1);
        FundingItem fundingItem2 = FundingItem.createFundingItem(funding, item2, 2);
        Field fundingItem = funding.getClass().getDeclaredField("fundingItems");
        fundingItem.setAccessible(true);
        fundingItem.set(funding, List.of(fundingItem1, fundingItem2));

        Contributor contributor = Contributor.createContributor(10000, friend, funding);

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(fundingRepository.findFundingByMemberId(member.getMemberId())).thenReturn(List.of(funding));
        when(contributorRepository.countContributorsForFunding(funding.getFundingId())).thenReturn(1L);
        //when
        MyFundingHistoryDto myFundingHistoryDto = fundingService.getMyFundingHistory(member.getMemberId());
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);
        MyPageFundingDetailHistoryDto myPageFundingDetailHistoryDto = MyPageFundingDetailHistoryDto.fromEntity(funding,
                1L);
        //then
        assertEquals(myPageMemberDto, myFundingHistoryDto.myPageMemberDto());
        assertEquals(myPageFundingDetailHistoryDto, myFundingHistoryDto.myPageFundingDetailHistoryDtos().get(0));
    }

    @DisplayName("내 펀딩 이력 조회-성공(펀딩 이력 없음)")
    @Test
    void getMyFundingHistory_NotFoundFunding() {
        //given
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(fundingRepository.findFundingByMemberId(member.getMemberId())).thenReturn(List.of());
        //when
        MyFundingHistoryDto myFundingHistoryDto = fundingService.getMyFundingHistory(member.getMemberId());
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);
        //then
        assertEquals(myPageMemberDto, myFundingHistoryDto.myPageMemberDto());
        assertEquals(0, myFundingHistoryDto.myPageFundingDetailHistoryDtos().size());
    }

    @DisplayName("내 펀딩 이력 조회-성공(펀딩 기여자 없음)")
    @Test
    void getMyFundingHistory_NotFoundContributor() throws NoSuchFieldException, IllegalAccessException {
        //given
        Funding funding = Funding.createFunding(
                member, "생일 축하해줘", Tag.BIRTHDAY, 112000,
                LocalDateTime.of(2024, 5, 14, 23, 59));
        Field fundingId1 = funding.getClass().getDeclaredField("fundingId");
        fundingId1.setAccessible(true);
        fundingId1.set(funding, 1L);
        Field fundingStatus = funding.getClass().getDeclaredField("fundingStatus");
        fundingStatus.setAccessible(true);
        fundingStatus.set(funding, false);
        Field createdDateField = BaseTimeEntity.class.getDeclaredField("createdDate");
        createdDateField.setAccessible(true);
        createdDateField.set(funding, LocalDateTime.now());

        FundingItem fundingItem1 = FundingItem.createFundingItem(funding, item1, 1);
        FundingItem fundingItem2 = FundingItem.createFundingItem(funding, item2, 2);
        Field fundingItem = funding.getClass().getDeclaredField("fundingItems");
        fundingItem.setAccessible(true);
        fundingItem.set(funding, List.of(fundingItem1, fundingItem2));

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(fundingRepository.findFundingByMemberId(member.getMemberId())).thenReturn(List.of(funding));
        when(contributorRepository.countContributorsForFunding(funding.getFundingId())).thenReturn(0L);
        //when
        MyFundingHistoryDto myFundingHistoryDto = fundingService.getMyFundingHistory(member.getMemberId());
        MyPageMemberDto myPageMemberDto = MyPageMemberDto.fromEntity(member);
        MyPageFundingDetailHistoryDto myPageFundingDetailHistoryDto = MyPageFundingDetailHistoryDto.fromEntity(funding,
                0L);
        //then
        assertEquals(myPageMemberDto, myFundingHistoryDto.myPageMemberDto());
        assertEquals(myPageFundingDetailHistoryDto, myFundingHistoryDto.myPageFundingDetailHistoryDtos().get(0));
    }

    @DisplayName("내 펀딩 이력 조회-실패(사용자를 찾을 수 없음)")
    @Test
    void getMyFundingHistory_NotFoundMember() {
        //given
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.empty());
        //when
        CommonException exception = assertThrows(CommonException.class, () -> {
            fundingService.getMyFundingHistory(member.getMemberId());
        });

        //then
        assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
    }

    private static Item createItemId(
            Long itemId1,
            String itemName,
            int itemPrice,
            String itemImageUrl,
            String brandName,
            String category,
            String optionName
    ) throws NoSuchFieldException, IllegalAccessException {
        Item item = Item.createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
        Field itemId = item.getClass().getDeclaredField("itemId");
        itemId.setAccessible(true);
        itemId.set(item, itemId1);
        return item;
    }

    private Member createMember() {
        return Member.createMember("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }

    private List<Relationship> getMyRelationships(Member friend) {
        List<Relationship> relationships = Relationship.createRelationships(member, friend);
        List<Relationship> myRelationships = new ArrayList<>();
        myRelationships.add(relationships.get(0));
        return myRelationships;
    }


}