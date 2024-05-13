package kcs.funding.fundingboost.domain.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import jakarta.servlet.http.HttpServletRequest;
import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.login.LoginDto;
import kcs.funding.fundingboost.domain.dto.request.login.SignupDto;
import kcs.funding.fundingboost.domain.dto.response.login.JwtAccessTokenDto;
import kcs.funding.fundingboost.domain.dto.response.login.UsernamePasswordJwtDto;
import kcs.funding.fundingboost.domain.security.service.SimpleAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SimpleAuthenticationService simpleAuthenticationService;

    /**
     * 토큰 없이 로그인 시도
     */
    @PostMapping("/signin")
    public ResponseDto<UsernamePasswordJwtDto> signIn(@RequestBody LoginDto loginDto) {
        return ResponseDto.ok(simpleAuthenticationService.createJwtToken(loginDto));
    }

    /**
     * 토큰을 이용한 로그인 시도
     */
    @PostMapping("/signup")
    public ResponseDto<CommonSuccessDto> signUp(@RequestBody SignupDto signupDto) {
        return ResponseDto.ok(simpleAuthenticationService.signup(signupDto));
    }

    /**
     * access token 재발급
     */
    @PostMapping("/access-reissue")
    public ResponseDto<JwtAccessTokenDto> reIssueAccessToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(AUTHORIZATION);
        simpleAuthenticationService.createAccessToken(refreshToken);
    }
}
