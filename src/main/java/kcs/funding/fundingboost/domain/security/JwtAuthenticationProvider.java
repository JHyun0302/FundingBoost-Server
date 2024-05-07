package kcs.funding.fundingboost.domain.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Base64;
import java.util.List;
import kcs.funding.fundingboost.domain.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;

    public boolean validateToken(String token) {
        try {

            String[] parts = token.split("\\.");
            String payLoad = parts[1];
            String signature = parts[2];

            String decodedPayload = new String(Base64.getDecoder().decode(payLoad));

            String calculatedJwt = Jwts.builder()
                    .signWith(JwtUtils.getKey(), SignatureAlgorithm.HS512)
                    .setHeaderParam("typ", "JWT")
                    .setPayload(decodedPayload)
                    .compact();

            String calculatedSignature = calculatedJwt.split("\\.")[2];

            return signature.equals(calculatedSignature);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException exception) {
            log.info("잘못된 JWT 서명");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못됨");
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JwtUtils.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserDetails principal = customUserDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(principal, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
