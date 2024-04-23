package kcs.funding.fundingboost.domain.dto.request;

import java.util.List;
import kcs.funding.fundingboost.domain.dto.response.ItemDto;

public record OrderItemsDto(
        List<ItemDto> itemDtos,
        List<Long> giftHubItemIds
) {

}
