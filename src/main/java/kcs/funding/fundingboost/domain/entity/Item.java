package kcs.funding.fundingboost.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "item")
public class Item {
    @Id
    @NotNull
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
}
