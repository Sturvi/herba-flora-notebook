package com.example.inovasiyanotebook.model.order;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.service.viewservices.order.OrderPositionConverter;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "raw_order_data")
@Getter
@SuperBuilder
public class RawOrderData extends AbstractEntity {

    public RawOrderData() {
        this.positions = new ArrayList<>();
        this.isProcessed = false;
    }

    public RawOrderData(Integer orderNumber, LocalDate orderDate, List<RawPositionData> positions) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.positions = positions;
        this.isProcessed = false;
    }

    @Column(name = "order_number")
    @Setter
    private Integer orderNumber;

    @Column(name = "order_date")
    @Setter
    private LocalDate orderDate;


    @Convert(converter = OrderPositionConverter.class)
    @Column(name = "positions", columnDefinition = "TEXT")
    private List<RawPositionData> positions;

    @Column(name = "is_processed")
    @Setter
    private Boolean isProcessed;

    public void addPosition(RawPositionData position) {
        this.positions.add(position);
    }

    public boolean hasPosition() {
        return !this.positions.isEmpty();
    }
}
