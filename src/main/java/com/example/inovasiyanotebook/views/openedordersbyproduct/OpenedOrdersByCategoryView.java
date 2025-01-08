package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Comparator;
import java.util.List;

@PageTitle("Kateqoriya üzrə açıq sifarişlər\n")
@Route(value = "orders-by-category", layout = MainLayout.class)
@PermitAll
@RequiredArgsConstructor
public class OpenedOrdersByCategoryView extends VerticalLayout {

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
        openedPositions.forEach(categoryOpeningPositionDTOList::addProductPosition);

        categoryOpeningPositionDTOList.getSortedCategoryPositions().forEach(dto -> {
            var card = layoutProvider.getObject();
            card.setCategoryOpeningPositionDTO(dto);
            add(card.getLayout());
        });

    }

    public static List<ProductOpeningPositionDTO> sortByCategoryAndDate(List<ProductOpeningPositionDTO> dtoList) {
        dtoList.sort(Comparator.comparing((ProductOpeningPositionDTO dto) ->
            dto.getProduct().getCategory().hasParent() ? dto.getProduct().getCategory().getParent().getName() : dto.getProduct().getCategory().getName())
            .thenComparing(dto -> dto.getProduct().getCategory().getName())
            .thenComparing(ProductOpeningPositionDTO::getEarliestOrderReceivedDate));
        return dtoList;
    }

}
