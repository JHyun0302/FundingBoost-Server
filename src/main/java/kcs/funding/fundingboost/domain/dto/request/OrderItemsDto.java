package kcs.funding.fundingboost.domain.dto.request;

import kcs.funding.fundingboost.domain.dto.response.ItemDto;

import java.util.List;

public record OrderItemsDto(
        List<ItemDto> items,
        List<Long> giftHubItemIds
) {

}
