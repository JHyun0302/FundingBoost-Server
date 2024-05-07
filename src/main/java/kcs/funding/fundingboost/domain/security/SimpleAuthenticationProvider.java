package kcs.funding.fundingboost.domain.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleAuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;

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
}
