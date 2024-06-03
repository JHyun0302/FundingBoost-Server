package kcs.funding.fundingboost.elasticsearch.index;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Document(indexName = "item")
public class ItemIndex {

    @Id
    private Long itemId;

    @Field(name = "item_name", type = FieldType.Text)
    private String itemName;

    @Field(name = "item_price", type = FieldType.Integer)
    private int itemPrice;

    @Field(name = "item_image_url", type = FieldType.Text)
    private String itemImageUrl;

    @Field(name = "brand_name", type = FieldType.Text)
    private String brandName;

    @Field(name = "category", type = FieldType.Keyword)
    private String category;

    @Field(name = "option_name", type = FieldType.Text)
    private String optionName;
}
