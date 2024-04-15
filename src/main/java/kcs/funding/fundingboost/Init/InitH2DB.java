package kcs.funding.fundingboost.Init;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
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
//        initService.init();
        initService.mainPageTestInit();
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

        public void mainPageTestInit() {
            Member member1 = Member.createMember("nickname", "email@gmail.com", "url", 20000);
            Member member2 = Member.createMember("friend1", "email2@gmail.com", "url2", 22000);
            Member member3 = Member.createMember("friend2", "email3@gmail.com", "url3", 23000);
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

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

            // 친구 관계 설정
            List<Relationship> relationshipList = Relationship.createRelationships(member1, member2);
            for (Relationship relationship : relationshipList) {
                em.persist(relationship);
            }

            // 내 펀딩 정보 추가
            Funding myFunding = Funding.createFunding(member1, "생일축하해줘", Tag.BIRTHDAY, 100000,
                LocalDate.now().plusDays(14));
            em.persist(myFunding);

            FundingItem fundingItem1 = FundingItem.createFundingItem(myFunding, item1, 1);
            FundingItem fundingItem2 = FundingItem.createFundingItem(myFunding, item2, 2);
            em.persist(fundingItem1);
            em.persist(fundingItem2);

            // 친구 펀딩 정보 추가
            Funding friend1Funding = Funding.createFunding(member2, "드디어 졸업 성공~~", Tag.GRADUATE, 200000,
                LocalDate.now().plusDays(7));
            em.persist(friend1Funding);

            FundingItem friendFundingItem1 = FundingItem.createFundingItem(friend1Funding, item3, 1);
            em.persist(friendFundingItem1);
        }
    }
}
