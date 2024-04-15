package kcs.funding.fundingboost.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

import kcs.funding.fundingboost.domain.entity.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "funding")
public class Funding extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funding_id")
    private Long fundingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(length = 50)
    private String message;

    @Enumerated(EnumType.STRING)
    private Tag tag;

    @NotNull
    @Column(name = "total_price")
    private int totalPrice;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "collect_price")
    private int collectPrice;

    @NotNull
    private LocalDateTime deadline;

    @NotNull
    @Column(name = "funding_status")
    private boolean fundingStatus;

    public static Funding createFunding(Member member, String message, Tag tag, int totalPrice, LocalDateTime deadline){
        Funding funding = new Funding();
        funding.member = member;
        funding.message = message;
        funding.tag = tag;
        funding.totalPrice = totalPrice;
        funding.collectPrice = 0;
        funding.deadline = deadline;
        funding.fundingStatus = true;
        return funding;
    }
}
