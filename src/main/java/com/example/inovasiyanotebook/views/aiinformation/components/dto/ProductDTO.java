package com.example.inovasiyanotebook.views.aiinformation.components.dto;

import lombok.Data;

import java.util.Map;

@Data
public class   ProductDTO {
    private String name;

    private String ts;

    private String barcode;

    private String weight;

    private String shelfLife;

    private String price;

    private Map<String, String> extraInfo;

}
