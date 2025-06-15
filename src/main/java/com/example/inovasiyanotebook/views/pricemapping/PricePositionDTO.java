package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PricePositionDTO {
    private String positionName;
    private String price;
    private ProductPriceMapping productPriceMapping;

    public PricePositionDTO(String positionName, String price) {
        this.positionName = positionName;
        this.price = price;
    }
}
