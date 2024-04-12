package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
