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
        name = "relationship"
)
public class Relationship {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "relation_id")
    private Long relationId;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = LAZY)
    private Member member;

    @JoinColumn(name = "friend_id")
    @ManyToOne(fetch = LAZY)
    private Member friend;
}
