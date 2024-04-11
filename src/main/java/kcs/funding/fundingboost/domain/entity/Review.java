package kcs.funding.fundingboost.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "review"
)
public class Review {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    private int rating;

    @Column(length = 100)
    private String content;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = LAZY)
    private Member member;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = LAZY)
    private Item item;

}
