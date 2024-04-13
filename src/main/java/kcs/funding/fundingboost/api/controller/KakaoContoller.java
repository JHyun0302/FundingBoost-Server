package kcs.funding.fundingboost.api.controller;

import kcs.funding.fundingboost.api.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class KakaoContoller {

    private final KakaoService kakaoService;

    @RequestMapping("/login")
    public RedirectView goKakaoOAuth() {
        return kakaoService.goKakaoOAuth();
    }

    @RequestMapping("/login-callback")
    public RedirectView loginCallback(@RequestParam("code") String code) {
        return kakaoService.loginCallback(code);
    }

    @GetMapping("/profile")
    public String getProfile() {
        return kakaoService.getProfile();
    }

    @GetMapping("/friends")
    public String getFriends() {
        return kakaoService.getFriends();
    }

    @RequestMapping("/logout")
    public String logout() {
        return kakaoService.logout();
    }
}
