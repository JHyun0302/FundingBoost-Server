package kcs.funding.fundingboost.Init;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitH2DB {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.init();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    @Slf4j
    static class InitService {

        private final EntityManager em;

        public void init() {
            Member member = Member.createMember("nickname", "email@gmail.com", "url", 20000);
            em.persist(member);

            Item item1 = Item.createItem("그릭요거트 딸기 생크림 피스 + 아메리카노 (R)", 10700,
                    "https://gift.kakao.com/product/5543372", "투썸플레이스",
                    "카페", "");

            Item item2 = Item.createItem("[예스24] 양수인간 : 삶의 격을 높이는 내면 변화 심리학", 17820
                    , "https://gift.kakao.com/product/9443715", "인문",
                    "책", "");

            Item item3 = Item.createItem(
                    "[각인/선물포장] NEW 디올 어딕트 립스틱", 57000,
                    "https://gift.kakao.com/product/4418907", "디올",
                    "화장", "[NEW] 481 데지");
            em.persist(item1);
            em.persist(item2);
            em.persist(item3);

            GiftHubItem giftHubItem1 = GiftHubItem.createGiftHubItem(1, item1, member);
            GiftHubItem giftHubItem2 = GiftHubItem.createGiftHubItem(1, item2, member);
            em.persist(giftHubItem1);
            em.persist(giftHubItem2);
        }
    }
}
