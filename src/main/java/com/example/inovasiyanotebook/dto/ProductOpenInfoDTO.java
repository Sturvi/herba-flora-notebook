package com.example.inovasiyanotebook.dto;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import java.time.LocalDate;
import java.util.List;

public class ProductOpenInfoDTO {

    private final Product product;
    private final List<OrderPosition> openOrderPositions;
    private final List<ChangeTaskItem> openChangeTaskItems;
    private final LocalDate earliestOrderReceivedDate;
    private final Category parentCategory;

    public ProductOpenInfoDTO(
            Product product,
            List<OrderPosition> openOrderPositions,
            List<ChangeTaskItem> openChangeTaskItems,
            LocalDate earliestOrderReceivedDate,
            Category parentCategory) {
        this.product = product;
        this.openOrderPositions = openOrderPositions;
        this.openChangeTaskItems = openChangeTaskItems;
        this.earliestOrderReceivedDate = earliestOrderReceivedDate;
        this.parentCategory = parentCategory;
    }

    // Геттеры

    public Product getProduct() {
        return product;
    }

    public List<OrderPosition> getOpenOrderPositions() {
        return openOrderPositions;
    }

    public List<ChangeTaskItem> getOpenChangeTaskItems() {
        return openChangeTaskItems;
    }

    public LocalDate getEarliestOrderReceivedDate() {
        return earliestOrderReceivedDate;
    }

    public Category getParentCategory() {
        return parentCategory;
    }
}
