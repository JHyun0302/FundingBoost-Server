package kcs.funding.fundingboost.domain.repository;

import java.util.List;
import java.util.Optional;
import kcs.funding.fundingboost.domain.entity.FriendPayBarcodeToken;
import kcs.funding.fundingboost.domain.entity.FriendPayBarcodeTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendPayBarcodeTokenRepository extends JpaRepository<FriendPayBarcodeToken, Long> {
    Optional<FriendPayBarcodeToken> findByBarcodeToken(String barcodeToken);

    List<FriendPayBarcodeToken> findAllByMemberMemberIdAndFundingFundingIdAndTokenStatus(
            Long memberId,
            Long fundingId,
            FriendPayBarcodeTokenStatus tokenStatus
    );
}

