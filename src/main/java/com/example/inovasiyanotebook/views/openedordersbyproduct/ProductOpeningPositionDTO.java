package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;

public class ProductOpeningPositionDTO {
    private final Product product;
    @Getter
    private TreeSet<OrderPosition> openedPositions;
    @Getter
    private LocalDate orderReceivedDate;

    public ProductOpeningPositionDTO(Product product) {
        this.product = product;
        this.openedPositions = new TreeSet<>(Comparator.comparing(op -> op.getOrder().getOrderReceivedDate()));
        this.orderReceivedDate = LocalDate.now();
    }

    public void addOpenedPosition(OrderPosition orderPosition){
        this.openedPositions.add(orderPosition);

        if (orderPosition.getOrder().getOrderReceivedDate().isBefore(this.orderReceivedDate)) {
            this.orderReceivedDate = orderPosition.getOrder().getOrderReceivedDate();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductOpeningPositionDTO that = (ProductOpeningPositionDTO) o;
        return Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(product);
    }
}
