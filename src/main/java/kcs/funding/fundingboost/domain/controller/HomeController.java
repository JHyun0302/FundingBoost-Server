package kcs.funding.fundingboost.domain.controller;

import kcs.funding.fundingboost.domain.dto.global.ResponseDto;
import kcs.funding.fundingboost.domain.dto.response.HomeViewDto;
import kcs.funding.fundingboost.domain.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    private final HomeService homeService;

    /**
     * 메인페이지 조회
     */
    @GetMapping("")
    public ResponseDto<HomeViewDto> home(@RequestParam("memberId") Long memberId) {
        return ResponseDto.ok(homeService.getMainView(memberId));
    }
}
