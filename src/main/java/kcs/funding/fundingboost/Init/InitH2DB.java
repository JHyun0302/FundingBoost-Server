package kcs.funding.fundingboost.Init;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.entity.FundingItem;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import kcs.funding.fundingboost.domain.entity.Relationship;
import kcs.funding.fundingboost.domain.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class InitH2DB {
    private final InitService initService;

    @PostConstruct
    public void initDatabase() {
        List<Member> members = initService.initMember();
        List<Item> items = initService.initBeauty();
        initService.initOrders(members, items);
        initService.initDelivery(members);
        initService.initFunding(members, items);
        initService.initRelationships(members);
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    @Slf4j
    static class InitService {

        private final EntityManager em;

        public void initOrders(List<Member> members, List<Item> items) {
            Item item1 = items.get(0);
            Item item2 = items.get(1);
            Member member1 = members.get(0);
            Member member2 = members.get(0);

            Order order1 = Order.createOrder(1, 10700, item1, member1);
            Order order2 = Order.createOrder(1, 17820, item2, member2);
            em.persist(order1);
            em.persist(order2);
        }

        public void initDelivery(List<Member> members) {
            Member member1 = members.get(0);

            Delivery delivery1 = Delivery.createDelivery("서울시 가산 디지털단지", "010-1111-1111", "nickname1", member1);
            Delivery delivery2 = Delivery.createDelivery("경기도 오산시", "010-2222-2222", "nickname2", member1);
            em.persist(delivery1);
            em.persist(delivery2);
        }

        public void initFunding(List<Member> members, List<Item> items) {
            Item item1 = items.get(0);
            Item item2 = items.get(1);
            Member member1 = members.get(0);
            Member member2 = members.get(1);

            GiftHubItem giftHubItem1 = GiftHubItem.createGiftHubItem(1, item1, member1);
            GiftHubItem giftHubItem2 = GiftHubItem.createGiftHubItem(1, item2, member1);
            em.persist(giftHubItem1);
            em.persist(giftHubItem2);

            Funding funding1 = Funding.createFunding(member1, "생일축하해줘", Tag.BIRTHDAY, 100000,
                LocalDateTime.now().plusDays(14));
            em.persist(funding1);

            Funding funding2 = Funding.createFunding(member2, "드디어 졸업 성공~~", Tag.GRADUATE, 200000,
                LocalDateTime.now().plusDays(7));
            em.persist(funding2);

            Funding funding3 = Funding.createFundingWithCollectPrice(member1, "생일 축하~", Tag.BIRTHDAY, 100000,
                10000, LocalDateTime.now().plusDays(14));
            em.persist(funding3);

            Funding funding4 = Funding.createFundingWithCollectPrice(member2, "드디어 졸업 성공~~", Tag.GRADUATE, 200000,
                110000, LocalDateTime.now().plusDays(7));
            em.persist(funding4);

            FundingItem fundingItem1 = FundingItem.createFundingItem(funding1, item1, 1);
            FundingItem fundingItem2 = FundingItem.createFundingItem(funding1, item2, 2);
            em.persist(fundingItem1);
            em.persist(fundingItem2);

            FundingItem fundingItem3 = FundingItem.createFundingItem(funding3, item1, 1);
            FundingItem fundingItem4 = FundingItem.createFundingItem(funding3, item2, 2);
            em.persist(fundingItem3);
            em.persist(fundingItem4);

            FundingItem fundingItem5 = FundingItem.createFundingItem(funding4, item1, 1);
            FundingItem fundingItem6 = FundingItem.createFundingItem(funding4, item2, 2);
            em.persist(fundingItem5);
            em.persist(fundingItem6);
        }

        public void initRelationships(List<Member> members) {
            Member member1 = members.get(0);
            Member member2 = members.get(1);

            List<Relationship> relationshipList = Relationship.createRelationships(member1,
                member2);
            for (Relationship relationship : relationshipList) {
                em.persist(relationship);
            }
        }

        public List<Member> initMember() {
            List<Member> memberInfos = Arrays.asList(
                Member.createMember("구태형", "rnxogud136@gmail.com", "",
                    "https://p.kakaocdn.net/th/talkp/wowkAlwbLn/Ko25X6eV5bs1OycAz7n9Q1/lq4mv6_110x110_c.jpg",
                    "", "aFtpX2lZaFhvQ3JLe0J2QnFDcFxtXWhdbldgDA"),
                Member.createMember("맹인호", "aoddlsgh98@gmail.com", "",
                    "https://p.kakaocdn.net/th/talkp/woBG0lIJfU/M6aVERkQ2Lv2sNfQaLMYrK/pzfmfl_110x110_c.jpg",
                    "", "aFluW29Ya1hpRXdBdEdyQHBGdlprW25baFFmDQ"),
                Member.createMember("양혜인", "helen66626662@gmail.com", "",
                    "https://p.kakaocdn.net/th/talkp/woGALKKcHt/jiOhwZDs9RTkkXPwNYjxF1/wzruf2_110x110_c.jpg",
                    "", "aFtpXm1ZaVtuQnRMeUp9Tn5PY1JiV2JRaF8z"),
                Member.createMember("이재현", "jhyun030299@gmail.com", "",
                    "http://k.kakaocdn.net/dn/jrT50/btsF9BGMPni/7oxQfq58KmKxIl8UX01mn0/img_110x110.jpg", "",
                    "aFpqUmZVYFRsQHFIfU53R3ZDdlprW25baFFmDw"),
                Member.createMember("임창희", "dlackdgml3710@gmail.com", "",
                    "https://p.kakaocdn.net/th/talkp/wnbbRhlyRW/XaGAXxS1OkUtXnomt6S4IK/ky0f9a_110x110_c.jpg",
                    "", "aFxoWGFUZlV5SH9MfE9-TH1PY1JiV2JRaF83"),
                Member.createMember("현세미", "gustpal08@gmail.com", "", "", "",
                    "aFlvVm9bbFpoRHBGf0Z0RHRDb15uW25dZFM_")
            );

            for (Member member : memberInfos) {
                em.persist(member);
            }
            return memberInfos;
        }

        public List<Item> initBeauty() {
            List<Item> itemInfos = Arrays.asList(
                Item.createItem("NEW 루쥬 알뤼르 벨벳 뉘 블랑쉬 리미티드 에디션", 61000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240319133310_1fda0cf74e4f43608184bce3050ae22a.jpg",
                    "샤넬", "뷰티", "00:00"),

                Item.createItem("NEW 루쥬 코코 밤(+샤넬 기프트 카드)", 51000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220111185052_b92447cb764d470ead70b2d0fe75fe5c.jpg",
                    "샤넬", "뷰티", "934 코랄린 [NEW]"),

                Item.createItem("코코 마드모아젤 헤어 미스트 35ml", 85000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20230221174618_235ba31681ad4af4806ae974884abb99.jpg",
                    "샤넬", "뷰티", "코코 마드모아젤 헤어 미스트 35ml"),

                Item.createItem("레 베쥬 립 밤", 85000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220316102858_5380e2c951fc4661a0ec7b08a0bc96ee.jpg",
                    "샤넬", "뷰티", "미디엄"),

                Item.createItem("[단독+각인+포장] 입생로랑 1위 NEW 벨벳 틴트 세트(+리브르 향수 1.2ml)", 49000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240327134440_58d0d7e4b2ea4baebae1b4a3c065198c.jpg",
                    "입생로랑", "뷰티", "220 컨트롤 블러시 (NEW - 로지 코랄)"),

                Item.createItem("[선물포장] 리브르 핸드 크림 30ml", 33000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240415092310_78cc616347524a47b0cbc3f799a6193b.jpg",
                    "입생로랑", "뷰티", "[선물포장] 리브르 핸드 크림 30ml"),

                Item.createItem("[각인+포장] NEW 엉크르 드 뽀 쿠션 세트(+라운드 파우치)", 108000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240327140332_02835b48b1bc4faeaaaaad51f48aa62b.jpg",
                    "입생로랑", "뷰티", "10호"),

                Item.createItem("[단독/리미티드선물포장] 코롱 9ML", 32000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240411173509_791cf65118c142c68621829f4e2a42df.jpg",
                    "조말론런던", "뷰티", "블랙베리 앤 베이 코롱 9ML"),

                Item.createItem("[선물포장] 바디 크림 50ML", 45000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240315092736_2ffa91bc2e0d4430bb0fa69db5d2a431.jpg",
                    "조말론런던", "뷰티", "블랙베리 앤 베이 바디 크림 50ML"),

                Item.createItem("[단독각인/슬리브선물포장] 코롱 30ML", 110000,
                    "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240411170225_64f314ccd05a4570a90add7ee46480b4.jpg",
                    "조말론런던", "뷰티", "블랙베리 앤 베이 코롱 30ML"));

            for (Item item : itemInfos) {
                em.persist(item);
            }
            return itemInfos;
        }
    }
}