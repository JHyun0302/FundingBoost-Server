package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
