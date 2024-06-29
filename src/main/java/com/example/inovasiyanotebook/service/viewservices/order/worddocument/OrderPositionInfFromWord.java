package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class OrderPositionInfFromWord {

    private Integer positionNo;
    private String positionName;
    private Integer quantity;
    private LocalDate shelfLife;
    private String note;
}
