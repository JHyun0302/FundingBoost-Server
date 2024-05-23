package kcs.funding.fundingboost.domain.repository.giftHubItem;


import static org.assertj.core.api.Assertions.assertThat;

import kcs.funding.fundingboost.domain.config.QueryDslConfig;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.member.Member;
import kcs.funding.fundingboost.domain.model.GiftHubItemFixture;
import kcs.funding.fundingboost.domain.model.ItemFixture;
import kcs.funding.fundingboost.domain.model.MemberFixture;
import kcs.funding.fundingboost.domain.repository.ItemRepository;
import kcs.funding.fundingboost.domain.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
class GiftHubItemRepositoryTest {

    @Autowired
    private GiftHubItemRepository giftHubItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Member member;
    private Item item;
    private GiftHubItem giftHubItem;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        member = MemberFixture.member1();
        item = ItemFixture.item1();
    }

    @AfterEach
    void after() {
        giftHubItemRepository.deleteAll();
        itemRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void findGiftHubItemByGiftHubItemIdAndMemberId() throws NoSuchFieldException, IllegalAccessException {

        Member savedMember = memberRepository.save(member);
        Item savedItem = itemRepository.save(item);
        giftHubItem = GiftHubItemFixture.quantity1(savedItem, savedMember);
        GiftHubItem savedGiftHubItem = giftHubItemRepository.save(giftHubItem);

        //when
        GiftHubItem result = giftHubItemRepository.findGiftHubItemByGiftHubItemIdAndMemberId(
                savedGiftHubItem.getGiftHubItemId(),
                savedMember.getMemberId()).get();
        //then
        assertThat(result.getGiftHubItemId()).isEqualTo(savedGiftHubItem.getGiftHubItemId());
        assertThat(result.getMember().getNickName()).isEqualTo(savedMember.getNickName());
        assertThat(result.getItem().getItemName()).isEqualTo(savedItem.getItemName());
    }
}