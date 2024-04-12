package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.response.HomeViewDto;
import kcs.funding.fundingboost.domain.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/home")
    public HomeViewDto home(Long memberId) {
        return homeService.getMainView(memberId);
    }
}
