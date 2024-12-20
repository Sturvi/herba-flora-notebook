package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@PageTitle("Aciq sifarisler olan mehsullar")
@Route(value = "orders-by-product", layout = MainLayout.class)
@PermitAll
@RequiredArgsConstructor
public class OpenedOrdersByProduct  extends HorizontalLayout implements HasUrlParameter<String> {

    private final OrderPositionService orderPositionService;


    @Override
    public void setParameter(BeforeEvent event, String id) {
        removeAll();

        if (id == null) {

        } else {

        }
    }

    private void handleHasntProduct() {
        var openedPositions
    }

    @PostConstruct
    private void setupOpenedOrdersByProduct() {
        setHeightFull();
        setHeightFull();
    }
}
