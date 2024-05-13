package kcs.funding.fundingboost.domain.security.service;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.accessTokenValidityInMilliseconds;
import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.refreshTokenValidityInMilliseconds;
import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.secret;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import kcs.funding.fundingboost.domain.entity.token.RefreshToken;
import kcs.funding.fundingboost.domain.repository.token.RefreshTokenRepository;
import kcs.funding.fundingboost.domain.security.CustomUserDetails;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthenticationService implements InitializingBean {

    private RefreshTokenRepository refreshTokenRepository;

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
    public String createAccessToken(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        long now = (new Date()).getTime();
        Date accessTokenValidity = new Date(now + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setHeaderParam("typ", "JWT")
                .setSubject(String.valueOf(principal.getMemberId()))
                .setExpiration(accessTokenValidity)
                .setIssuedAt(new Date())
                .compact();
    }

    public RefreshToken createRefreshToken(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = principal.getMemberId();
        long now = (new Date()).getTime();
        Date refreshTokenValidity = new Date(now + refreshTokenValidityInMilliseconds);

        String refreshToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setExpiration(refreshTokenValidity)
                .compact();
        return RefreshToken.createRefreshToken(refreshToken, memberId);
    }

    /**
     * accessToken 생성
     */
//    public static String createAccessToken(Authentication authentication) {
//
//    }
}
