package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.common.CommonSuccessDto;
import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.request.login.LoginDto;
import kcs.funding.fundingboost.domain.dto.request.login.SignupDto;
import kcs.funding.fundingboost.domain.dto.response.login.TokenDto;
import kcs.funding.fundingboost.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@RequestBody LoginDto loginDto) {
        return authService.createJwtToken(loginDto);
    }

    @PostMapping("/signup")
    public ResponseDto<CommonSuccessDto> register(@RequestBody SignupDto signupDto) {
        return ResponseDto.ok(authService.signup(signupDto));
    }
}
