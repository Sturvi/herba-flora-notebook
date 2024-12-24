package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

@Getter
public class ProductOpeningPositionDTO {
    private final Product product;
    private TreeSet<OrderPosition> openedPositions;
    private LocalDate orderReceivedDate;
    private final Category parentCategory;

    public ProductOpeningPositionDTO(Product product) {
        this.product = product;

        Category topCategory = product.getCategory();
        while (topCategory.hasParent()) {
            topCategory = topCategory.getParentCategory();
        }
        this.parentCategory = topCategory;

        this.openedPositions = new TreeSet<>(Comparator.comparing(op -> op.getOrder().getOrderReceivedDate()));
        this.orderReceivedDate = LocalDate.now();
    }

    public void addOpenedPosition(OrderPosition orderPosition) {
        if (orderPosition.getProduct().equals(product)) {
            this.openedPositions.add(orderPosition);

            if (orderPosition.getOrder().getOrderReceivedDate().isBefore(this.orderReceivedDate)) {
                this.orderReceivedDate = orderPosition.getOrder().getOrderReceivedDate();
            }
        }
    }

    public List<OrderPosition> getOpenedPositions() {
        return openedPositions.stream().toList();
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
