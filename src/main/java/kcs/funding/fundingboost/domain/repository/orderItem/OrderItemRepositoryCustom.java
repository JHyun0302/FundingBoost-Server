package kcs.funding.fundingboost.domain.repository.orderItem;

import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.entity.OrderItem;

public interface OrderItemRepositoryCustom {
    Optional<List<OrderItem>> findLastOrderByMemberId(Long memberId);
}
