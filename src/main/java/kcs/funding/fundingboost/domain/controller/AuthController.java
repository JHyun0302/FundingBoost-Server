package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.login.LoginDto;
import kcs.funding.fundingboost.domain.dto.request.login.SignupDto;
import kcs.funding.fundingboost.domain.dto.response.login.UsernamePasswordJwtDto;
import kcs.funding.fundingboost.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 토큰 없이 로그인 시도
     */
    @PostMapping("/signin")
    public ResponseDto<UsernamePasswordJwtDto> signIn(@RequestBody LoginDto loginDto) {
        return ResponseDto.ok(authService.createJwtToken(loginDto));
    }

    /**
     * 토큰을 이용한 로그인 시도
     */
    @PostMapping("/signup")
    public ResponseDto<CommonSuccessDto> signUp(@RequestBody SignupDto signupDto) {
        return ResponseDto.ok(authService.signup(signupDto));
    }
}
