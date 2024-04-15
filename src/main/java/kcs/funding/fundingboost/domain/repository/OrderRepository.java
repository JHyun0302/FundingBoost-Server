package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Member;
import kcs.funding.fundingboost.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o" +
            " join fetch o.member m" +
            " where m.memberId = :memberId")
    List<Order> findAllByMemberId(@Param("memberId")Long memberId);
}
