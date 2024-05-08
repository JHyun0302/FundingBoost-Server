package kcs.funding.fundingboost.domain.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 토큰이 없는 경우 nickname과 password를 이용해 검증 만약 비밀번호가 동일하지 않다면 예외를 던진다
     */
    public CustomUserDetails validate(String username, String password) {
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (password.equals(userDetails.getPassword())) {
            return userDetails;
        } else {
            throw new RuntimeException("user login fail");
        }
    }

    /**
     * request 정보의 authentication 이용해 인증 정보를 가진 authentication 생성
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String nickname = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(nickname);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
