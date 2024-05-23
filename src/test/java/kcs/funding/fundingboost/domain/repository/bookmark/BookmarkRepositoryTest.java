package kcs.funding.fundingboost.domain.repository.bookmark;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
@Slf4j
class BookmarkRepositoryTest {
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Member member;
    private Item item;


    @Autowired
    private MemberRepository memberRepository;

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
    void findAllByMemberId() throws NoSuchFieldException, IllegalAccessException {
        Member savedMember = memberRepository.save(member);
        Item savedItem = itemRepository.save(item);
        Bookmark bookmark = BookmarkFixture.bookmark1(savedMember, savedItem);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        //when
        List<Bookmark> bookmarkList = new ArrayList<>();
        bookmarkList.add(savedBookmark);
        List<Bookmark> result = bookmarkRepository.findAllByMemberId(savedMember.getMemberId());
        //then
        assertThat(result.size()).isEqualTo(bookmarkList.size());
        assertThat(result.get(0).getMember().getMemberId()).isEqualTo(savedMember.getMemberId());
        assertThat(result.get(0).getItem().getItemId()).isEqualTo(savedItem.getItemId());
    }
}