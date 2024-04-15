package kcs.funding.fundingboost.Init;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import kcs.funding.fundingboost.domain.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Console;
import java.time.LocalDate;


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
    static class InitService{

        private final EntityManager em;

        public void init() {

            Member member = Member.createMember("nickname", "email@gmail.com", "url");
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


            Order order1 = Order.createOrder(1, 10700, item1, member);
            Order order2 = Order.createOrder(1, 17820, item2, member);
            em.persist(order1);
            em.persist(order2);


            Delivery delivery1  = Delivery.createDelivery("서울시 가산 디지털단지", "010-1111-1111", "nickname1", member, item1);
            Delivery delivery2  = Delivery.createDelivery("경기도 오산시", "010-2222-2222", "nickname2", member, item2);
            em.persist(delivery1);
            em.persist(delivery2);


            Funding funding = Funding.createFunding(member, "Thanks", Tag.BIRTHDAY, 57000, LocalDate.of(2023,12,27));
            em.persist(funding);

            FundingItem fundingItem = FundingItem.createFundgindItem(funding,item3,1);
            em.persist(fundingItem);


        }
    }
}
