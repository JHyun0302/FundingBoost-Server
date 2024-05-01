package kcs.funding.fundingboost.domain.service;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.AUTHORIZATION_HEADER;
import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.TOKEN_PREFIX;

import kcs.funding.fundingboost.domain.dto.request.login.LoginDto;
import kcs.funding.fundingboost.domain.dto.response.login.TokenDto;
import kcs.funding.fundingboost.domain.security.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public ResponseEntity<TokenDto> createJwtToken(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDto.nickName(),
                loginDto.password());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
        SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(authentication);

        String jwt = jwtAuthenticationProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, TOKEN_PREFIX + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}
