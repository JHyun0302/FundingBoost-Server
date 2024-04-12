package kcs.funding.fundingboost.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kcs.funding.fundingboost.domain.entity.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item")
public class Item extends BaseTimeEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @NotEmpty
    @Column(name = "item_name", length = 100)
    private String itemName;

    @NotNull
    @Column(name = "item_price")
    private int itemPrice;

    @NotEmpty
    @Column(name = "item_image_url", length = 100)
    private String itemImageUrl;

    @NotEmpty
    @Column(name = "brand_name", length = 100)
    private String brandName;

    @NotEmpty
    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "option_name", length = 100)
    private String optionName;

    private Item(String itemName, int itemPrice, String itemImageUrl, String brandName, String category,
                 String optionName) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemImageUrl = itemImageUrl;
        this.brandName = brandName;
        this.category = category;
        this.optionName = optionName;
    }

    public static Item createItem(String itemName, int itemPrice, String itemImageUrl, String brandName,
                                  String category, String optionName) {
        return new Item(itemName, itemPrice, itemImageUrl, brandName, category, optionName);
    }
}
