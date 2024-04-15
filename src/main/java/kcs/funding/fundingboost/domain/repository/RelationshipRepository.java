package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
}
