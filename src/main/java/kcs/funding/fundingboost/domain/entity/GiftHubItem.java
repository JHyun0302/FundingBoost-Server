package kcs.funding.fundingboost.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "giftHub_item")
public class GiftHubItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "giftHub_item_id")
    private Long giftHunItemId;

    @ColumnDefault("1")
    @Column(name = "quantity")
    private int quantity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
