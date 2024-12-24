package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.client.Category;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryOpeningPositionDTO {
    @Getter
    private final Category category;
    @Getter
    private List<ProductOpeningPositionDTO> positionList;
    @Getter
    private LocalDate orderReceivedDate;

    public CategoryOpeningPositionDTO(Category category) {
        this.category = category;
        this.positionList = new ArrayList<>();
        this.orderReceivedDate = LocalDate.now();
    }

    public void addOpenedPosition(ProductOpeningPositionDTO productOpeningPositionDTO){
        if (productOpeningPositionDTO.getParentCategory().equals(category)) {
            positionList.add(productOpeningPositionDTO);
            if (productOpeningPositionDTO.getOrderReceivedDate().isBefore(this.orderReceivedDate)) {
                this.orderReceivedDate = productOpeningPositionDTO.getOrderReceivedDate();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryOpeningPositionDTO that = (CategoryOpeningPositionDTO) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(category);
    }
}
