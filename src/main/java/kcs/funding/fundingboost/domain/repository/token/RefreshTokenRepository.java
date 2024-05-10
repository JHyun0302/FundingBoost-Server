package kcs.funding.fundingboost.domain.repository.token;

import kcs.funding.fundingboost.domain.entity.token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
