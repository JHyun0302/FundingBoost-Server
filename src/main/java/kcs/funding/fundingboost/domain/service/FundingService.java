package kcs.funding.fundingboost.domain.service;

import kcs.funding.fundingboost.domain.dto.common.commonSuccessDto;
import kcs.funding.fundingboost.domain.entity.Funding;
import kcs.funding.fundingboost.domain.repository.funding.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FundingService {

    private final FundingRepository fundingRepository;

    public commonSuccessDto terminateFunding(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId)
            .orElseThrow(() -> new RuntimeException("Funding not found"));
        funding.terminate();
        return commonSuccessDto.fromEntity(true);
    }
}
