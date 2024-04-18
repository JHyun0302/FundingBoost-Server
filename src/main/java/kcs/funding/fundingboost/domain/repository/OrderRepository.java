package kcs.funding.fundingboost.domain.repository;

import java.util.List;
import kcs.funding.fundingboost.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o" +
            " join fetch o.member m" +
            " where m.memberId = :memberId")
    List<Order> findAllByMemberId(@Param("memberId") Long memberId);
}
