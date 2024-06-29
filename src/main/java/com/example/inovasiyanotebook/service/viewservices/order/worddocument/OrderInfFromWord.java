package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderInfFromWord {

    @Getter
    @Setter
    private Integer orderNumber;
    @Getter
    @Setter
    private LocalDate orderDate;
    @Getter
    @Setter
    private String department;
    @Getter
    private List<OrderPositionInfFromWord> positions;

    public OrderInfFromWord() {
        this.positions = new ArrayList<>();
    }

    public void addPosition(OrderPositionInfFromWord position) {
        this.positions.add(position);
    }

    public boolean hasPosition (){
        return !this.positions.isEmpty();
    }
}