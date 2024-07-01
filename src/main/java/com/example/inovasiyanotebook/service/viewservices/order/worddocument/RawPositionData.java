package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RawPositionData implements Serializable {

    private Integer positionNo;
    private String positionName;
    private Integer quantity;
    private String shelfLife;
    private String note;
}
