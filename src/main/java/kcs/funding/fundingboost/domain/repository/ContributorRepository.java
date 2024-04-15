package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {
}
