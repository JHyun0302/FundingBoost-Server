package kcs.funding.fundingboost.domain.security;

import static kcs.funding.fundingboost.domain.exception.ErrorCode.EXPIRED_TOKEN_ERROR;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.INVALID_TOKEN_ERROR;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.TOKEN_MALFORMED_ERROR;
import static kcs.funding.fundingboost.domain.exception.ErrorCode.TOKEN_UNSUPPORTED_ERROR;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.List;
import kcs.funding.fundingboost.domain.exception.CommonException;
import kcs.funding.fundingboost.domain.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(JwtUtils.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException exception) {
            throw new CommonException(TOKEN_MALFORMED_ERROR);
        } catch (ExpiredJwtException e) {
            throw new CommonException(EXPIRED_TOKEN_ERROR);
        } catch (UnsupportedJwtException e) {
            throw new CommonException(INVALID_TOKEN_ERROR);
        } catch (IllegalArgumentException e) {
            throw new CommonException(TOKEN_UNSUPPORTED_ERROR);
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JwtUtils.getKey())
                .build()
                .parseClaimsJws((String) authentication.getPrincipal())
                .getBody();

        long userId = Long.parseLong(claims.getSubject());

        CustomUserDetails principal = customUserDetailsService.loadUserByUserId(userId);

        return new UsernamePasswordAuthenticationToken(principal, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
