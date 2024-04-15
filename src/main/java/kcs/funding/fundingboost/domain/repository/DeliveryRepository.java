package kcs.funding.fundingboost.domain.repository;

import kcs.funding.fundingboost.domain.dto.response.DeliveryDto;
import kcs.funding.fundingboost.domain.entity.Delivery;
import kcs.funding.fundingboost.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("select d from Delivery d" +
            " join fetch d.member m" +
            " where m.memberId= :memberId")
    List<Delivery> findAllByMemberId(@Param("memberId")Long memberId);
}
