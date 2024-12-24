package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;

import java.util.*;

@PageTitle("Aciq sifarisler olan mehsullar")
@Route(value = "orders-by-product", layout = MainLayout.class)
@PermitAll
@RequiredArgsConstructor
public class OpenedOrdersByProduct extends VerticalLayout {

    private final OrderPositionService orderPositionService;
    private final ObjectProvider<CategoriesOpenedOrdersCardLayout> layoutProvider;

    private TreeGrid<Object> treeGrid;

    @PostConstruct
    private void setupOpenedOrdersByProduct() {
        setHeightFull();

        // Initialize TreeGrid
        treeGrid = new TreeGrid<>();
        treeGrid.setWidthFull();
        treeGrid.setHeightFull();

        // Fetch data
        List<ProductOpeningPositionDTO> openedPositions = orderPositionService.getAllWithOpeningStatusGroupedByProduct();

        CategoryOpeningPositionDTOList categoryOpeningPositionDTOList = new CategoryOpeningPositionDTOList();
        openedPositions.forEach(categoryOpeningPositionDTOList::addProductOpeningPositionDTO);

        categoryOpeningPositionDTOList.getCategoryOpeningPositionDTOList().forEach(dto -> {
            var card = layoutProvider.getObject();
            card.setCategoryOpeningPositionDTO(dto);
            add(card.getLayout());
        });

    }

    public static List<ProductOpeningPositionDTO> sortByCategoryAndDate(List<ProductOpeningPositionDTO> dtoList) {
        dtoList.sort(Comparator.comparing((ProductOpeningPositionDTO dto) ->
            dto.getProduct().getCategory().hasParent() ? dto.getProduct().getCategory().getParent().getName() : dto.getProduct().getCategory().getName())
            .thenComparing(dto -> dto.getProduct().getCategory().getName())
            .thenComparing(ProductOpeningPositionDTO::getOrderReceivedDate));
        return dtoList;
    }

}
