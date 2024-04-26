package kcs.funding.fundingboost.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.response.ItemDetailDto;
import kcs.funding.fundingboost.domain.dto.response.ShopDto;
import kcs.funding.fundingboost.domain.entity.Bookmark;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.repository.Bookmark.BookmarkRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ItemService itemService;

    @DisplayName("아이템 조회")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션', 61000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg', '샤넬', '뷰티', '00:00'",
            "'NEW 루쥬 코코 밤(+샤넬 기프트 카드)', 51000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg', '샤넬', '뷰티', '934 코랄린 [NEW]'"
    })
    void getItems(String itemName, int itemPrice, String itemImageUrl, String brandName, String category,
                  String optionName) {
        //given
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category,
                optionName);
        List<Item> items = Collections.singletonList(item);
        when(itemRepository.findAll()).thenReturn(items);

        //when
        List<ShopDto> result = itemService.getItems();

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findAll();
    }

    @DisplayName("아이템 상세 조회 : 북마크가 존재하는 경우")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션', 61000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg', '샤넬', '뷰티', '00:00'",
            "'NEW 루쥬 코코 밤(+샤넬 기프트 카드)', 51000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg', '샤넬', '뷰티', '934 코랄린 [NEW]'"
    })
    void getItemDetail_WhenBookmarkExists(String itemName, int itemPrice, String itemImageUrl, String brandName,
                                          String category, String optionName) {
        //given
        Member member = createMember();
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category,
                optionName);
        Bookmark bookmark = Bookmark.createBookmark(member, item);

        when(bookmarkRepository.findBookmarkByMemberAndItem(member.getMemberId(), item.getItemId())).thenReturn(
                Optional.of(bookmark));

        //when
        ItemDetailDto result = itemService.getItemDetail(member.getMemberId(), item.getItemId());

        //then
        assertNotNull(result);
        Assertions.assertTrue(result.bookmark());
        verify(bookmarkRepository, times(1)).findBookmarkByMemberAndItem(member.getMemberId(), item.getItemId());
    }

    @DisplayName("아이템 상세 조회 : 북마크가 존재하지 않는 경우")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션', 61000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg', '샤넬', '뷰티', '00:00'",
            "'NEW 루쥬 코코 밤(+샤넬 기프트 카드)', 51000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg', '샤넬', '뷰티', '934 코랄린 [NEW]'"
    })
    void getItemDetail_WhenBookmarkDoesNotExist(String itemName, int itemPrice, String itemImageUrl, String brandName,
                                                String category, String optionName) {
        //given
        Member member = createMember();
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category,
                optionName);

        when(bookmarkRepository.findBookmarkByMemberAndItem(member.getMemberId(), item.getItemId()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(item.getItemId()))
                .thenReturn(Optional.of(item));

        //when
        ItemDetailDto result = itemService.getItemDetail(member.getMemberId(), item.getItemId());

        //then
        assertNotNull(result);
        Assertions.assertFalse(result.bookmark());
        verify(itemRepository, times(1)).findById(item.getItemId());
    }

    @DisplayName("북마크가 존재할 때 : 북마크 삭제")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "'NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션', 61000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg', '샤넬', '뷰티', '00:00'",
            "'NEW 루쥬 코코 밤(+샤넬 기프트 카드)', 51000, 'https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg', '샤넬', '뷰티', '934 코랄린 [NEW]'"
    })
    void givenBookmarkExists_whenToggleItemLike_thenDeleteBookmark(String itemName, int itemPrice, String itemImageUrl,
                                                                   String brandName,
                                                                   String category, String optionName) {
        //given
        Member member = createMember();
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category,
                optionName);
        Bookmark bookmark = Bookmark.createBookmark(member, item);

        when(bookmarkRepository.findBookmarkByMemberAndItem(member.getMemberId(), item.getItemId()))
                .thenReturn(Optional.of(bookmark));

        //when
        CommonSuccessDto result = itemService.toggleItemLike(member.getMemberId(), item.getItemId());

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
                                                                      String itemImageUrl,
                                                                      String brandName,
                                                                      String category, String optionName) {
        //given
        Member member = createMember();
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category,
                optionName);

        when(bookmarkRepository.findBookmarkByMemberAndItem(member.getMemberId(), item.getItemId())).thenReturn(
                Optional.empty());
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));

        //when
        CommonSuccessDto result = itemService.toggleItemLike(member.getMemberId(), item.getItemId());

        // then
        //bookmarkRepository의 save 메소드가 한 번 호출되었는지 확인하고, 그 호출에 사용된 파라미터의 타입이 Bookmark 클래스의 인스턴스임을 확인하지만, 구체적인 인스턴스 값까지는 검사하지 않는다.
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
        assertTrue(result.isSuccess());
    }

    private static Member createMember() {
        Member member = Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
        return member;
    }

    private static Item createItem(String itemName, int itemPrice, String itemImageUrl, String brandName,
                                   String category,
                                   String optionName) {
        Item item = Item.createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
        return item;
    }
}