package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftHubItemRepository extends JpaRepository<GiftHubItem, Long> {
}
