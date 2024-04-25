package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem , Long> {
}
