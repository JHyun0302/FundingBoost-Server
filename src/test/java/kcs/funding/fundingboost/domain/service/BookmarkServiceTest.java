package kcs.funding.fundingboost.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.response.myPage.wishList.MyBookmarkListDto;
import kcs.funding.fundingboost.domain.entity.Bookmark;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.bookmark.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    private Member member;

    private Bookmark bookmark1;

    private Bookmark bookmark2;

    private Item item1;

    private Item item2;

    @BeforeEach
    void setUp() {
        member = createMember();
        item1 = createItem1();
        item2 = createItem2();
    }

    @DisplayName("북마크 존재 시: 북마크 목록 조회 성공")
    @Test
    public void getMyWishList_Success() {
        //given
        ReflectionTestUtils.setField(member, "memberId", 1L);
        ReflectionTestUtils.setField(item1, "itemId", 1L);
        ReflectionTestUtils.setField(item2, "itemId", 2L);

        Bookmark bookmark1 = Bookmark.createBookmark(member, item1);
        Bookmark bookmark2 = Bookmark.createBookmark(member, item2);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        when(bookmarkRepository.findAllByMemberId(member.getMemberId())).thenReturn(List.of(bookmark1, bookmark2));

        //when
        MyBookmarkListDto resultDto = bookmarkService.getMyWishList(member.getMemberId());

        //then
        assertNotNull(resultDto);
        assertThat(resultDto.myPageMemberDto().nickname()).isEqualTo(member.getNickName());
        assertThat(resultDto.bookmarkItemDtos()).hasSize(2).extracting("itemId")
                .contains(item1.getItemId(), item2.getItemId());
    }

    @DisplayName("북마크가 존재하지 않을 시: 북마크 빈 리스트 값으로 반환")
    @Test
    public void getMyWishList_Null() {
        //given
        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));

        //when
        MyBookmarkListDto resultDto = bookmarkService.getMyWishList(member.getMemberId());

        //then
        assertNotNull(resultDto);
        assertThat(resultDto.myPageMemberDto().nickname()).isEqualTo(member.getNickName());
        assertThat(resultDto.bookmarkItemDtos()).isEmpty();
    }

    @DisplayName("북마크가 존재할 때 : 북마크 삭제")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션', 61000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg', '샤넬', '뷰티', '00:00'",
            "'NEW 루쥬 코코 밤(+샤넬 기프트 카드)', 51000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg', '샤넬', '뷰티', '934 코랄린 [NEW]'"
    })
    void givenBookmarkExists_whenToggleItemLike_thenDeleteBookmark(String itemName, int itemPrice,
                                                                   String itemImageUrl, String brandName,
                                                                   String category, String optionName) {
        //given
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
        Bookmark bookmark = Bookmark.createBookmark(member, item);

        when(bookmarkRepository.findBookmarkByMemberAndItem(member.getMemberId(), item.getItemId()))
                .thenReturn(Optional.of(bookmark));

        //when
        CommonSuccessDto result = bookmarkService.toggleItemLike(member.getMemberId(), item.getItemId());

        // then
        verify(bookmarkRepository, times(1)).delete(bookmark);
        assertTrue(result.isSuccess());
    }

    @DisplayName("북마크가 존재하지 않을 때 : 북마크 저장")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션', 61000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg', '샤넬', '뷰티', '00:00'",
            "'NEW 루쥬 코코 밤(+샤넬 기프트 카드)', 51000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg', '샤넬', '뷰티', '934 코랄린 [NEW]'"})
    void givenBookmarkNotExists_whenToggleItemLike_thenCreateBookmark(String itemName, int itemPrice,
                                                                      String itemImageUrl, String brandName,
                                                                      String category, String optionName) {
        //given
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);

        when(bookmarkRepository.findBookmarkByMemberAndItem(member.getMemberId(), item.getItemId())).thenReturn(
                Optional.empty());
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));

        //when
        CommonSuccessDto result = bookmarkService.toggleItemLike(member.getMemberId(), item.getItemId());

        // then
        //bookmarkRepository의 save 메소드가 한 번 호출되었는지 확인하고, 그 호출에 사용된 파라미터의 타입이 Bookmark 클래스의 인스턴스임을 확인하지만, 구체적인 인스턴스 값까지는 검사하지 않는다.
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
        assertTrue(result.isSuccess());
    }

    private static Member createMember() {
        return Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
    }

    private static Item createItem(String itemName, int itemPrice, String itemImageUrl, String brandName,
                                   String category, String optionName) {
        return Item.createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
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


}