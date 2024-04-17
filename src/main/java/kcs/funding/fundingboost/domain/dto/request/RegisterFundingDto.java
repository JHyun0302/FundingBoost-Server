package kcs.funding.fundingboost.domain.dto.request;


import java.time.LocalDateTime;
import java.util.List;

public record RegisterFundingDto(
        List<Long> itemIdList,
        String fundingMessage,
        String tag,
        LocalDateTime deadline
) {

}
