package kcs.funding.fundingboost.domain.security.utils;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.secret;
import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.tokenValidityInMilliseconds;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import kcs.funding.fundingboost.domain.security.CustomUserDetails;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils implements InitializingBean {

    @Getter
    private static Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰 생성
     */
    public static String createToken(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds);

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setHeaderParam("typ", "JWT")
                .setSubject(String.valueOf(principal.getMemberId()))
                .setExpiration(validity)
                .setIssuedAt(new Date())
                .compact();
    }
}
