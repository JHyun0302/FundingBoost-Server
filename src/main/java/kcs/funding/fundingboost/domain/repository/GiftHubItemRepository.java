package kcs.funding.fundingboost.domain.repository;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.GiftHubItem;
import kcs.funding.fundingboost.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftHubItemRepository extends JpaRepository<GiftHubItem, Long> {

    List<GiftHubItem> findGiftHubItemsByMember(Member member);

}
