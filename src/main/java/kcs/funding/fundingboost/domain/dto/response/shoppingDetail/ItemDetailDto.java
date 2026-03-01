package kcs.funding.fundingboost.domain.dto.response.shoppingDetail;

import kcs.funding.fundingboost.domain.entity.Item;
import kcs.funding.fundingboost.elasticsearch.index.ItemIndex;
import lombok.Builder;

@Builder
public record ItemDetailDto(
        String itemThumbnailImageUrl,
        String itemName,
        int itemPrice,
        boolean bookmark,
        String option
) {
    public static ItemDetailDto fromEntity(Item item, boolean bookmark) {
        return ItemDetailDto.builder()
                .itemThumbnailImageUrl(item.getItemImageUrl())
                .itemName(item.getItemName())
                .itemPrice(item.getItemPrice())
                .bookmark(bookmark)
                .option(item.getOptionName())
                .build();
    }

    public static ItemDetailDto fromIndex(ItemIndex itemIndex, boolean bookmark) {
        return ItemDetailDto.builder()
                .itemThumbnailImageUrl(itemIndex.getItemImageUrl())
                .itemName(itemIndex.getItemName())
                .itemPrice(itemIndex.getItemPrice())
                .bookmark(bookmark)
                .option(itemIndex.getOptionName())
                .build();
    }
}
