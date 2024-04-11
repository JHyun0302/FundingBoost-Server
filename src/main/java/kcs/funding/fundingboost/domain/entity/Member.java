package kcs.funding.fundingboost.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "member"
)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
