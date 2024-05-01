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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.request.giftHub.AddGiftHubDto;
import kcs.funding.fundingboost.domain.dto.request.giftHub.ItemQuantityDto;
import kcs.funding.fundingboost.domain.dto.response.giftHub.GiftHubDto;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import kcs.funding.fundingboost.domain.repository.giftHubItem.GiftHubItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    private Item item1;
    private Item item2;
    private GiftHubItem giftHubItem1;
    private GiftHubItem giftHubItem2;

    @BeforeEach
    void setUp() {
        member = createMember();
        item1 = createItem1();
        item2 = createItem2();
        giftHubItem1 = GiftHubItem.createGiftHubItem(1, item1, member);
        giftHubItem2 = GiftHubItem.createGiftHubItem(2, item2, member);
    }

    @DisplayName("로그인 한 사용자의 기프트 허브 정보")
    @Test
    void getGiftHub_ReturnsGiftHubDtoList_WhenMemberExists() {
        //given
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
    @Test
    void addGiftHub() throws NoSuchFieldException, IllegalAccessException {
        //given
        Field itemId1 = item1.getClass().getDeclaredField("itemId");
        itemId1.setAccessible(true);
        itemId1.set(item1, 1L);

        Field itemId2 = item1.getClass().getDeclaredField("itemId");
        itemId2.setAccessible(true);
        itemId2.set(item2, 2L);
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
    @Test
    void addGiftHub_Fail_MemberNotFound() throws NoSuchFieldException, IllegalAccessException {
        // given
        //리플렉션을 이용한 itemId 강제 주입
        Field itemId = item1.getClass().getDeclaredField("itemId");
        itemId.setAccessible(true);
        itemId.set(item1, 1L);
        when(itemRepository.findById(item1.getItemId())).thenReturn(Optional.of(item1));
        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.empty());
        AddGiftHubDto addGiftHubDto = new AddGiftHubDto(member.getMemberId(), 1);

        // when
        Exception exception = assertThrows(CommonException.class, () -> {
            giftHubItemService.addGiftHub(item1.getItemId(), addGiftHubDto);
        });

        // then
        assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
        verify(giftHubItemRepository, times(0)).save(any(GiftHubItem.class));
    }

    @DisplayName("GiftHubItem 수량 변경 성공")
    @Test
    void updateItem_Success() {
        //given
        ItemQuantityDto itemQuantityDto = new ItemQuantityDto(10);
        GiftHubItem mockGiftHubItem = mock(GiftHubItem.class);

        when(giftHubItemRepository.findById(giftHubItem1.getGiftHunItemId())).thenReturn(Optional.of(mockGiftHubItem));

        //when
        CommonSuccessDto result = giftHubItemService.updateItem(giftHubItem1.getGiftHunItemId(), itemQuantityDto);

        //then
        verify(giftHubItemRepository).findById(giftHubItem1.getGiftHunItemId());
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

    @DisplayName("GiftHubItem 삭제 성공")
    @Test
    void deleteGiftHubItem_Success() {
        //given
        when(giftHubItemRepository.findGiftHubItemByGiftHubItemIdAndMemberId(giftHubItem1.getGiftHunItemId(),
                member.getMemberId())).thenReturn(Optional.of(giftHubItem1));

        //when
        CommonSuccessDto result = giftHubItemService.deleteGiftHubItem(member.getMemberId(),
                giftHubItem1.getGiftHunItemId());

        //then
        assertTrue(result.isSuccess());
        verify(giftHubItemRepository).deleteById(giftHubItem1.getGiftHunItemId());
    }

    @DisplayName("GiftHubItem 삭제 실패 - 아이템 조회 실패")
    @Test
    void deleteGiftHubItem_ItemNotFound() {
        //given
        when(giftHubItemRepository.findGiftHubItemByGiftHubItemIdAndMemberId(giftHubItem1.getGiftHunItemId(),
                member.getMemberId())).thenReturn(Optional.empty());

        //when
        CommonException exception = assertThrows(CommonException.class,
                () -> giftHubItemService.deleteGiftHubItem(member.getMemberId(), giftHubItem1.getGiftHunItemId()));

        //then
        assertEquals(NOT_FOUND_GIFTHUB_ITEM.getMessage(), exception.getMessage());
        verify(giftHubItemRepository, never()).deleteById(anyLong());
    }


    private static Member createMember() {
        return Member.createMemberWithPoint("임창희", "dlackdgml3710@gmail.com", "",
                "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                46000,
                "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83");
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
