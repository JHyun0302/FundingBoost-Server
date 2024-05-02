package kcs.funding.fundingboost.domain.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.fundingRegist.RegisterFundingDto;
import kcs.funding.fundingboost.domain.dto.response.common.CommonFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.common.FriendFundingPageItemDto;
import kcs.funding.fundingboost.domain.dto.response.friendFunding.FriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.friendFundingDetail.ContributorDto;
import kcs.funding.fundingboost.domain.dto.response.friendFundingDetail.FriendFundingDetailDto;
import kcs.funding.fundingboost.domain.dto.response.friendFundingDetail.FriendFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.fundingRegist.FundingRegistrationItemDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeFriendFundingDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeItemDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMemberInfoDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMyFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeMyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.home.HomeViewDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.MyPageMemberDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory.FriendFundingContributionDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory.FriendFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingHistory.MyFundingHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingHistory.MyPageFundingDetailHistoryDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.MyFundingHistoryDetailDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.MyFundingStatusDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.MyPageFundingItemDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.myFundingStatus.ParticipateFriendDto;
import kcs.funding.fundingboost.domain.entity.Contributor;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
import kcs.funding.fundingboost.domain.entity.common.BaseTimeEntity;
import kcs.funding.fundingboost.domain.service.FundingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FundingController.class)
public class FundingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FundingService fundingService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member1;
    private Member member2;
    private Member member3;
    private Item item1;
    private Item item2;
    private Funding funding1;
    private Funding funding2;
    private FundingItem fundingItem1;
    private FundingItem fundingItem2;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        createMember();
        createItem1();
        createItem2();
        funding1 = createFundingFundingStatusIsTrue(member1);
        funding2 = createFundingFundingStatusIsTrue(member2);
        createFundingItem1(funding1, item1);
        createFundingItem2(funding1, item2);
        createRelationship(member1, member2);
    }

    @DisplayName("메인페이지 조회")
    @Test
    void home() throws Exception {
        HomeMemberInfoDto homeMemberInfoDto = HomeMemberInfoDto.fromEntity(member1);
        HomeMyFundingStatusDto homeMyFundingStatusDto = HomeMyFundingStatusDto.fromEntity(funding1, "D-3");
        List<HomeMyFundingItemDto> homeMyFundingItemDtoList = List.of(
                HomeMyFundingItemDto.fromEntity(fundingItem1, 100), HomeMyFundingItemDto.fromEntity(fundingItem2, 50));
        List<HomeFriendFundingDto> homeFriendFundingDtoList = Collections.singletonList(
                HomeFriendFundingDto.fromEntity(
                        CommonFriendFundingDto.fromEntity(funding2, "D-7", 80, new ArrayList<>()), null));
        List<HomeItemDto> itemDtoList = List.of(HomeItemDto.fromEntity(item1), HomeItemDto.fromEntity(item2));

        HomeViewDto homeViewDto = HomeViewDto.fromEntity(homeMemberInfoDto, homeMyFundingStatusDto,
                homeMyFundingItemDtoList, homeFriendFundingDtoList, itemDtoList);

        given(fundingService.getMainView(member1.getMemberId())).willReturn(homeViewDto);

        mockMvc.perform(get("/api/v1/home")
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.homeMemberInfoDto.nickName").value("임창희"))
                .andExpect(jsonPath("$.data.homeMemberInfoDto.profile").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.homeMyFundingItemDtoList.length()").value(2))
                .andExpect(jsonPath("$.data.homeMyFundingItemDtoList[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data.homeMyFundingItemDtoList[0].itemPercent").value(100))
                .andExpect(jsonPath("$.data.homeMyFundingStatusDto.deadline").value("D-3"))
                .andExpect(jsonPath("$.data.homeFriendFundingDtoList.length()").value(1))
                .andExpect(jsonPath("$.data.homeFriendFundingDtoList[0].commonFriendFundingDto.fundingId").value(1L))
                .andExpect(jsonPath("$.data.homeFriendFundingDtoList[0].commonFriendFundingDto.nickName").value("구태형"))
                .andExpect(
                        jsonPath("$.data.homeFriendFundingDtoList[0].commonFriendFundingDto.friendProfileImgUrl")
                                .value("https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg"))
                .andExpect(jsonPath(
                        "$.data.homeFriendFundingDtoList[0].commonFriendFundingDto.friendFundingDeadlineDate")
                        .value("D-7"))
                .andExpect(jsonPath(
                        "$.data.homeFriendFundingDtoList[0].commonFriendFundingDto.friendFundingPercent").value(80))
                .andExpect(jsonPath(
                        "$.data.homeFriendFundingDtoList[0].commonFriendFundingDto.friendFundingPageItemDtoList.length()")
                        .value(0))
                .andExpect(jsonPath("$.data.itemDtoList.length()").value(2))
                .andExpect(jsonPath("$.data.itemDtoList[0].itemId").value(1))
                .andExpect(jsonPath("$.data.itemDtoList[0].itemName").value("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션"))
                .andExpect(jsonPath("$.data.itemDtoList[0].price").value(61000))
                .andExpect(jsonPath("$.data.itemDtoList[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data.itemDtoList[0].brandName").value("샤넬"));
    }

    @DisplayName("펀딩 등록 페이지 조회")
    @Test
    void viewFundingRegistration() throws Exception {
        List<Long> registerFundingBringItemDto = List.of(1L, 2L);
        List<FundingRegistrationItemDto> expectedDtoList = List.of(
                FundingRegistrationItemDto.createFundingRegistrationItemDto(item1, 1L),
                FundingRegistrationItemDto.createFundingRegistrationItemDto(item2, 2L));

        given(fundingService.getFundingRegister(registerFundingBringItemDto, member1.getMemberId())).willReturn(
                expectedDtoList);

        mockMvc.perform(get("/api/v1/funding")
                        .param("memberId", String.valueOf(member1.getMemberId()))
                        .param("ItemList", registerFundingBringItemDto.stream().map(Object::toString).toArray(String[]::new)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemId").value(1))
                .andExpect(jsonPath("$.data[0].itemSequence").value(1L))
                .andExpect(jsonPath("$.data[0].itemName").value("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션"))
                .andExpect(jsonPath("$.data[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data[0].optionName").value("00:00"))
                .andExpect(jsonPath("$.data[0].itemPrice").value(61000));
    }

    @DisplayName("펀딩 등록하기")
    @Test
    void registerFunding() throws Exception {
        RegisterFundingDto registerFundingDto = new RegisterFundingDto(List.of(1L, 2L), "생일", "생일",
                LocalDate.of(2024, 4, 30));
        CommonSuccessDto expectedResponse = CommonSuccessDto.fromEntity(true);

        when(fundingService.putFundingAndFundingItem(anyLong(), any(RegisterFundingDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/funding")
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerFundingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isSuccess").value(true));
    }

    @DisplayName("펀딩 종료하기")
    @Test
    void closeFunding() throws Exception {
        CommonSuccessDto expectedResponse = CommonSuccessDto.fromEntity(true);

        when(fundingService.terminateFunding(funding1.getFundingId())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/funding/close/{fundingId}", funding1.getFundingId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isSuccess").value(true));
    }

    @DisplayName("친구 펀딩 디테일 페이지 조회")
    @Test
    void viewFriendsFundingDetail() throws Exception {
        List<FriendFundingItemDto> friendFundingItemList = List.of(
                FriendFundingItemDto.fromEntity(fundingItem1), FriendFundingItemDto.fromEntity(fundingItem2));
        List<ContributorDto> contributorList = List.of(
                ContributorDto.fromEntity(Contributor.createContributor(20000, member3, funding1)));

        FriendFundingDetailDto
                friendFundingDetailDto = FriendFundingDetailDto.fromEntity(friendFundingItemList, funding1,
                contributorList, 80);
        when(fundingService.viewFriendsFundingDetail(funding1.getFundingId(), member1.getMemberId())).thenReturn(
                friendFundingDetailDto);

        mockMvc.perform(get("/api/v1/funding/friends/{fundingId}", funding1.getFundingId())
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.friendFundingItemList.length()").value(2))
                .andExpect(jsonPath("$.data.friendFundingItemList[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data.friendFundingItemList[0].itemId").value(1))
                .andExpect(jsonPath("$.data.friendFundingItemList[0].itemName").value("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션"))
                .andExpect(jsonPath("$.data.friendFundingItemList[0].optionName").value("00:00"))
                .andExpect(jsonPath("$.data.friendFundingItemList[0].itemPrice").value(61000));
    }

    @DisplayName("친구 펀딩 목록 조회")
    @Test
    void viewFriendFundingList() throws Exception {
        CommonFriendFundingDto commonFriendFundingDto = CommonFriendFundingDto.fromEntity(funding1, "D-3",
                80, List.of(FriendFundingPageItemDto.fromEntity(item1)));

        List<FriendFundingDto> friendFundingList = List.of(FriendFundingDto.fromEntity(commonFriendFundingDto));

        when(fundingService.getFriendFundingList(member1.getMemberId())).thenReturn(friendFundingList);

        // 실행 및 검증
        mockMvc.perform(get("/api/v1/funding/friends")
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.fundingId").value(1))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.nickName").value("구태형"))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.friendProfileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg"))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.friendFundingDeadlineDate").value("D-3"))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.tag").value("#생일"))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.collectPrice").value(0))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.friendFundingPercent").value(80))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.friendFundingPageItemDtoList.length()").value(1))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.friendFundingPageItemDtoList[0].itemPrice")
                        .value(61000))
                .andExpect(jsonPath("$.data[0].commonFriendFundingDto.friendFundingPageItemDtoList[0].itemImageUrl")
                        .value("https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"));
    }

    @DisplayName("펀딩 기간 늘리기")
    @Test
    void extendMyFunding() throws Exception {
        CommonSuccessDto expectedResponse = CommonSuccessDto.fromEntity(true);

        when(fundingService.extendFunding(funding1.getFundingId())).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/funding/extension/{fundingId}", funding1.getFundingId())
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isSuccess").value(true));
    }

    @DisplayName("마이 페이지 조회")
    @Test
    void viewMyPage() throws Exception {
        List<MyPageFundingItemDto> myPageFundingItemList = List.of(
                MyPageFundingItemDto.fromEntity(funding1, item1, 80, true));
        List<ParticipateFriendDto> participateFriendDtoList = List.of(
                ParticipateFriendDto.fromEntity(Contributor.createContributor(20000, member2, funding1)));

        MyFundingStatusDto myFundingStatusDto = MyFundingStatusDto.createMyFundingStatusDto(
                MyPageMemberDto.fromEntity(member1), myPageFundingItemList, participateFriendDtoList,
                80, "2024-05-02", "D-3");

        given(fundingService.getMyFundingStatus(member1.getMemberId())).willReturn(myFundingStatusDto);

        mockMvc.perform(get("/api/v1/funding/my-funding-status")
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.myPageMemberDto.nickName").value("임창희"))
                .andExpect(jsonPath("$.data.myPageMemberDto.email").value("dlackdgml3710@gmail.com"))
                .andExpect(jsonPath("$.data.myPageMemberDto.profileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.myPageMemberDto.point").value(46000))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList.length()").value(1))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].fundingId").value(1))
                .andExpect(
                        jsonPath("$.data.myPageFundingItemDtoList[0].itemName").value("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션"))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].itemPrice").value(61000))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].optionName").value("00:00"))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].itemPercent").value(80))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].finishedStatus").value(true))
                .andExpect(jsonPath("$.data.participateFriendDtoList.length()").value(1))
                .andExpect(jsonPath("$.data.participateFriendDtoList[0].participateNickname").value("구태형"))
                .andExpect(jsonPath("$.data.participateFriendDtoList[0].participatePrice").value(20000))
                .andExpect(jsonPath("$.data.participateFriendDtoList[0].participateProfileImgUrl")
                        .value("https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.totalPercent").value(80))
                .andExpect(jsonPath("$.data.deadline").value("2024-05-02"))
                .andExpect(jsonPath("$.data.deadlineDate").value("D-3"));
    }

    @DisplayName("지난 펀딩 이력 조회")
    @Test
    void viewMyFundingHistory() throws Exception {
        List<MyPageFundingDetailHistoryDto> myPageFundingDetailHistoryDtoList = List.of(
                MyPageFundingDetailHistoryDto.fromEntity(funding1, member2.getMemberId()));

        MyFundingHistoryDto myFundingHistoryDto = MyFundingHistoryDto.fromEntity(MyPageMemberDto.fromEntity(member1),
                myPageFundingDetailHistoryDtoList);
        given(fundingService.getMyFundingHistory(member1.getMemberId())).willReturn(myFundingHistoryDto);

        mockMvc.perform(get("/api/v1/funding/history")
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.myPageMemberDto.nickName").value("임창희"))
                .andExpect(jsonPath("$.data.myPageMemberDto.email").value("dlackdgml3710@gmail.com"))
                .andExpect(jsonPath("$.data.myPageMemberDto.profileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos.length()").value(1))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].fundingId").value(1))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].createdDate").value("2024-05-02"))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].deadLine").value("2024-05-16"))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].optionName").value("00:00"))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].status").value(true))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].contributorCount").value(2))
                .andExpect(jsonPath("$.data.myPageFundingDetailHistoryDtos[0].tag").value("#생일"));
    }

    @DisplayName("지난 펀딩 이력 상세 조회")
    @Test
    void viewMyFundingHistoryDetail() throws Exception {
        List<MyPageFundingItemDto> myPageFundingItemDtoList = List.of(
                MyPageFundingItemDto.fromEntity(funding1, item1, 80, true));
        List<ParticipateFriendDto> participateFriendDtoList = List.of(
                ParticipateFriendDto.fromEntity(Contributor.createContributor(20000, member2, funding1)));

        MyFundingHistoryDetailDto myFundingHistoryDetailDto = MyFundingHistoryDetailDto.createMyFundingHistoryDetailDto(
                MyPageMemberDto.fromEntity(member1), myPageFundingItemDtoList, participateFriendDtoList,
                90, "2024-05-02", "2024-05-16");

        Mockito.when(fundingService.getMyFundingHistoryDetails(member1.getMemberId(), funding1.getFundingId()))
                .thenReturn(myFundingHistoryDetailDto);

        mockMvc.perform(get("/api/v1/funding/history/{fundingId}", funding1.getFundingId())
                        .param("memberId", member1.getMemberId().toString())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.myPageMemberDto.nickName").value("임창희"))
                .andExpect(jsonPath("$.data.myPageMemberDto.email").value("dlackdgml3710@gmail.com"))
                .andExpect(jsonPath("$.data.myPageMemberDto.profileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList.length()").value(1))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].fundingId").value(1))
                .andExpect(
                        jsonPath("$.data.myPageFundingItemDtoList[0].itemName").value("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션"))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].itemPrice").value(61000))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].itemImageUrl").value(
                        "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg"))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].optionName").value("00:00"))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].itemPercent").value(80))
                .andExpect(jsonPath("$.data.myPageFundingItemDtoList[0].finishedStatus").value(true))
                .andExpect(jsonPath("$.data.participateFriendDtoList.length()").value(1))
                .andExpect(jsonPath("$.data.participateFriendDtoList[0].participateNickname").value("구태형"))
                .andExpect(jsonPath("$.data.participateFriendDtoList[0].participatePrice").value(20000))
                .andExpect(jsonPath("$.data.participateFriendDtoList[0].participateProfileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.totalPercent").value(90))
                .andExpect(jsonPath("$.data.createdDate").value("2024-05-02"))
                .andExpect(jsonPath("$.data.deadline").value("2024-05-16"));
    }

    @DisplayName("친구 펀딩 이력 조회")
    @Test
    void viewFriendFundingHistory() throws Exception {
        List<FriendFundingContributionDto> friendFundingContributionDto = List.of(
                kcs.funding.fundingboost.domain.dto.response.myPage.friendFundingHistory.FriendFundingContributionDto.fromEntity(
                        Contributor.createContributor(20000, member2, funding1), funding1));

        FriendFundingHistoryDto friendFundingHistoryDto = FriendFundingHistoryDto.fromEntity(
                MyPageMemberDto.fromEntity(member1), friendFundingContributionDto);

        given(fundingService.getFriendFundingHistory(member1.getMemberId())).willReturn(friendFundingHistoryDto);

        mockMvc.perform(get("/api/v1/funding/history/friend")
                        .param("memberId", "1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.myPageMemberDto.nickName").value("임창희"))
                .andExpect(jsonPath("$.data.myPageMemberDto.email").value("dlackdgml3710@gmail.com"))
                .andExpect(jsonPath("$.data.myPageMemberDto.profileImgUrl").value(
                        "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.myPageMemberDto.point").value(46000))
                .andExpect(jsonPath("$.data.FriendFundingContributionDto.length()").value(1))
                .andExpect(jsonPath("$.data.FriendFundingContributionDto[0].nickname").value("구태형"))
                .andExpect(jsonPath("$.data.FriendFundingContributionDto[0].price").value(20000))
                .andExpect(jsonPath("$.data.FriendFundingContributionDto[0].friendProfileImg").value(
                        "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg"))
                .andExpect(jsonPath("$.data.FriendFundingContributionDto[0].tag").value("#생일"))
                .andExpect(jsonPath("$.data.FriendFundingContributionDto[0].createdDate").value("2024-05-02"));
    }

    private void createMember() throws NoSuchFieldException, IllegalAccessException {
        member1 = Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");

        Field member1Id = member1.getClass().getDeclaredField("memberId");
        member1Id.setAccessible(true);
        member1Id.set(member1, 1L);

        member2 = Member.createMemberWithPoint("구태형", "rnxogud136@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg",
                999999999,
                "", "aFtpX2lZaFhvQ3JLe0J2QnFDcFxtXWhdbldgDA");

        Field member2Id = member2.getClass().getDeclaredField("memberId");
        member2Id.setAccessible(true);
        member2Id.set(member2, 2L);

        member3 = Member.createMemberWithPoint("맹인호", "aoddlsgh98@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/woBG0lIJfU/M6aVERkQ2Lv2sNfQaLMYrK/pzfmfl_110x110_c.jpg",
                200000,
                "", "aFluW29Ya1hpRXdBdEdyQHBGdlprW25baFFmDQ");

        Field member3Id = member3.getClass().getDeclaredField("memberId");
        member3Id.setAccessible(true);
        member3Id.set(member3, 3L);
    }

    private void createRelationship(Member member1, Member member2) {
        Relationship.createRelationships(member1, member2);
    }

    private void createItem1() throws NoSuchFieldException, IllegalAccessException {
        item1 = Item.createItem("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션", 61000,
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg",
                "샤넬", "뷰티", "00:00");

        Field item1Field = item1.getClass().getDeclaredField("itemId");
        item1Field.setAccessible(true);
        item1Field.set(item1, 1L);
    }

    private void createItem2() throws IllegalAccessException, NoSuchFieldException {
        item2 = Item.createItem("NEW 루쥬 코코 밤(+샤넬 기프트 카드)", 51000,
                "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg",
                "샤넬", "뷰티", "934 코랄린 [NEW]");

        Field item2Field = item2.getClass().getDeclaredField("itemId");
        item2Field.setAccessible(true);
        item2Field.set(item2, 2L);
    }

    private Funding createFundingFundingStatusIsTrue(Member member)
            throws IllegalAccessException, NoSuchFieldException {
        funding1 = Funding.createFunding(member, "생일축하해줘", Tag.BIRTHDAY, 100000,
                LocalDateTime.now().plusDays(14));

        Field fundingField = funding1.getClass().getDeclaredField("fundingId");
        fundingField.setAccessible(true);
        fundingField.set(funding1, 1L);

        Field createdDateField = BaseTimeEntity.class.getDeclaredField("createdDate");
        createdDateField.setAccessible(true);
        createdDateField.set(funding1, LocalDateTime.now());
        return funding1;
    }

    private void createFundingItem1(Funding funding, Item item)
            throws NoSuchFieldException, IllegalAccessException {
        fundingItem1 = FundingItem.createFundingItem(funding, item, 1);

        Field fundingItemId = fundingItem1.getClass().getDeclaredField("fundingItemId");
        fundingItemId.setAccessible(true);
        fundingItemId.set(fundingItem1, 1L);
    }

    private void createFundingItem2(Funding funding, Item item)
            throws NoSuchFieldException, IllegalAccessException {
        fundingItem2 = FundingItem.createFundingItem(funding, item, 2);

        Field fundingItemId = fundingItem2.getClass().getDeclaredField("fundingItemId");
        fundingItemId.setAccessible(true);
        fundingItemId.set(fundingItem2, 2L);
    }
}