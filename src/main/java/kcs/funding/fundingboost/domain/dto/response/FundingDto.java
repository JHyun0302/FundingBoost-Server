package kcs.funding.fundingboost.domain.dto.response;

import kcs.funding.fundingboost.domain.entity.Funding;

import lombok.Builder;

@Builder
public record FundingDto (int point,
                          int totalPrice){

    public static FundingDto fromEntity(Funding funding){
        return FundingDto.builder()
                .point(funding.getMember().getPoint())
                .totalPrice(funding.getTotalPrice())
                .build();
    }
}
