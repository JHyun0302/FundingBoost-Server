package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_GIFTHUB_ITEM;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_ITEM;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.AddGiftHubDto;
import kcs.funding.fundingboost.domain.dto.request.ItemQuantityDto;
import kcs.funding.fundingboost.domain.dto.response.GiftHubDto;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.GiftHubItem.GiftHubItemRepository;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
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
class GiftHubItemServiceTest {

    @Mock
    private GiftHubItemRepository giftHubItemRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private GiftHubItemService giftHubItemService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember();
    }

    @DisplayName("로그인 한 사용자의 기프트 허브 정보")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션, 61000, https://img1.kakaocdn.net/..., 샤넬, 뷰티, 00:00, NEW 루쥬 코코 밤(+샤넬 기프트 카드), 51000, https://img1.kakaocdn.net/..., 샤넬, 뷰티, 934 코랄린 [NEW]"
    })
    void getGiftHub_ReturnsGiftHubDtoList_WhenMemberExists(String itemName1, int itemPrice1, String itemImageUrl1,
                                                           String brandName1, String category1, String optionName1,
                                                           String itemName2, int itemPrice2, String itemImageUrl2,
                                                           String brandName2, String category2, String optionName2) {
        //given
        Item item1 = createItem(itemName1, itemPrice1, itemImageUrl1, brandName1, category1, optionName1);
        Item item2 = createItem(itemName2, itemPrice2, itemImageUrl2, brandName2, category2, optionName2);
        GiftHubItem giftHubItem1 = GiftHubItem.createGiftHubItem(1, item1, member);
        GiftHubItem giftHubItem2 = GiftHubItem.createGiftHubItem(2, item2, member);

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(giftHubItemRepository.findGiftHubItemsByMember(member.getMemberId())).thenReturn(
                List.of(giftHubItem1, giftHubItem2));

        // when
        List<GiftHubDto> result = giftHubItemService.getGiftHub(member.getMemberId());

        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        verify(memberRepository).findById(member.getMemberId());
        verify(giftHubItemRepository).findGiftHubItemsByMember(member.getMemberId());
    }

    @DisplayName("로그인 안한 사용자의 기프트 허브 정보")
    @Test
    public void getGiftHub_ThrowsException_WhenMemberDoesNotExist() {
        // given
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CommonException.class, () -> giftHubItemService.getGiftHub(member.getMemberId()));

        verify(memberRepository).findById(member.getMemberId());
        verifyNoInteractions(giftHubItemRepository);
    }

    @DisplayName("GiftHubItem 추가 성공")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({
            "NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션, 61000, https://img1.kakaocdn.net/..., 샤넬, 뷰티, 00:00, NEW 루쥬 코코 밤(+샤넬 기프트 카드), 51000, https://img1.kakaocdn.net/..., 샤넬, 뷰티, 934 코랄린 [NEW]"
    })
    void addGiftHub(String itemName1, int itemPrice1, String itemImageUrl1,
                    String brandName1, String category1, String optionName1,
                    String itemName2, int itemPrice2, String itemImageUrl2,
                    String brandName2, String category2, String optionName2)
            throws NoSuchFieldException, IllegalAccessException {

        //given
        Item item1 = createItem(itemName1, itemPrice1, itemImageUrl1, brandName1, category1, optionName1);
        Field itemId1 = item1.getClass().getDeclaredField("itemId");
        itemId1.setAccessible(true);
        itemId1.set(item1, 1L);
        Item item2 = createItem(itemName2, itemPrice2, itemImageUrl2, brandName2, category2, optionName2);
        Field itemId2 = item1.getClass().getDeclaredField("itemId");
        itemId2.setAccessible(true);
        itemId2.set(item1, 2L);
        AddGiftHubDto addGiftHubDto = new AddGiftHubDto(member.getMemberId(), 1);

        when(itemRepository.findById(item1.getItemId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(item2.getItemId())).thenReturn(Optional.of(item2));
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));

        // when
        CommonSuccessDto result1 = giftHubItemService.addGiftHub(item1.getItemId(), addGiftHubDto);
        CommonSuccessDto result2 = giftHubItemService.addGiftHub(item2.getItemId(), addGiftHubDto);

        // then
        assertNotNull(result1);
        assertTrue(result1.isSuccess());
        assertNotNull(result2);
        assertTrue(result2.isSuccess());
        verify(itemRepository).findById(item1.getItemId());
        verify(itemRepository).findById(item2.getItemId());
        verify(giftHubItemRepository, times(2)).save(any(GiftHubItem.class));
    }

    @DisplayName("GiftHubItem 추가 실패 - 아이템을 찾을 수 없음")
    @Test
    void addGiftHub_Fail_ItemNotFound() {
        // given
        AddGiftHubDto addGiftHubDto = new AddGiftHubDto(member.getMemberId(), 1);

        // when
        Exception exception = assertThrows(CommonException.class, () -> {
            giftHubItemService.addGiftHub(1L, addGiftHubDto);
        });

        // then
        assertEquals(NOT_FOUND_ITEM.getMessage(), exception.getMessage());
        verify(giftHubItemRepository, times(0)).save(any(GiftHubItem.class));
    }

    @DisplayName("GiftHubItem 추가 실패 - 멤버를 찾을 수 없음")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments}")
    @CsvSource({"NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션, 61000, https://img1.kakaocdn.net/..., 샤넬, 뷰티, 00:00"})
    void addGiftHub_Fail_MemberNotFound(String itemName, int itemPrice, String itemImageUrl,
                                        String brandName, String category, String optionName)
            throws NoSuchFieldException, IllegalAccessException {
        // given
        Item item = createItem(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
        //리플렉션을 이용한 itemId 강제 주입
        Field itemId = item.getClass().getDeclaredField("itemId");
        itemId.setAccessible(true);
        itemId.set(item, 1L);
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.empty());
        AddGiftHubDto addGiftHubDto = new AddGiftHubDto(member.getMemberId(), 1);

        // when
        Exception exception = assertThrows(CommonException.class, () -> {
            giftHubItemService.addGiftHub(item.getItemId(), addGiftHubDto);
        });

        // then
        assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
        verify(giftHubItemRepository, times(0)).save(any(GiftHubItem.class));
    }

    @DisplayName("GiftHubItem 수량 변경 성공")
    @Test
    void updateItem_Success() {
        //given
        Long gifthubItemId = 1L;
        ItemQuantityDto itemQuantityDto = new ItemQuantityDto(10);
        GiftHubItem mockGiftHubItem = mock(GiftHubItem.class);

        when(giftHubItemRepository.findById(gifthubItemId)).thenReturn(Optional.of(mockGiftHubItem));

        //when
        CommonSuccessDto result = giftHubItemService.updateItem(gifthubItemId, itemQuantityDto);

        //then
        verify(giftHubItemRepository).findById(gifthubItemId);
        verify(mockGiftHubItem).updateQuantity(itemQuantityDto.quantity());
        assertTrue(result.isSuccess());
    }

    @DisplayName("GiftHubItem 수량 변경 실패 - 아이템 조회 실패")
    @Test
    void updateItem_ItemNotFound() {
        // 준비
        Long gifthubItemId = 1L;
        ItemQuantityDto itemQuantityDto = new ItemQuantityDto(10);

        when(giftHubItemRepository.findById(gifthubItemId)).thenReturn(Optional.empty());

        // 실행 & 검증
        Exception exception = assertThrows(CommonException.class, () -> {
            giftHubItemService.updateItem(gifthubItemId, itemQuantityDto);
        });

        assertEquals(NOT_FOUND_GIFTHUB_ITEM.getMessage(), exception.getMessage());
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
}