package kcs.funding.fundingboost.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "item"
)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
