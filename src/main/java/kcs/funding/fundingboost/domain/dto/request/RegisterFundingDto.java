package kcs.funding.fundingboost.domain.dto.request;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RegisterFundingDto(
        List<RegisterFundingItemDto> registerFundingItemDtoList,
        String fundingMessage,
        String tag,
        int fundingTotalPrice,
        LocalDateTime deadline
) {

}
