package kcs.funding.fundingboost.domain.repository.bookmark;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import kcs.funding.fundingboost.domain.config.QueryDslConfig;
import kcs.funding.fundingboost.domain.entity.Bookmark;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.model.BookmarkFixture;
import kcs.funding.fundingboost.domain.model.ItemFixture;
import kcs.funding.fundingboost.domain.model.MemberFixture;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
@Slf4j
class BookmarkRepositoryImplTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Member member;
    private Item item;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = MemberFixture.member1();
        item = ItemFixture.item1();
    }


    @AfterEach
    void after() {
        bookmarkRepository.deleteAll();
        itemRepository.deleteAll();
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("멤버와 아이템으로 bookmark 찾기")
    void findBookmarkByMemberAndItem() throws NoSuchFieldException, IllegalAccessException {

        Member savedMember = memberRepository.save(member);
        Item savedItem = itemRepository.save(item);
        Bookmark bookmark = BookmarkFixture.bookmark1(savedMember, savedItem);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        //when
        Optional<Bookmark> result = bookmarkRepository.findBookmarkByMemberAndItem(savedMember.getMemberId(),
                savedBookmark.getItem().getItemId());

        //then
        assertThat(result.get().getMember().getNickName()).isEqualTo(savedBookmark.getMember().getNickName());
        assertThat(result.get().getItem().getItemName()).isEqualTo(savedItem.getItemName());

    }
}