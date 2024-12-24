package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.textfield.TextField;

@Service
@Scope("prototype")
public class ProductOrderCardLayout {
    private ProductOpeningPositionDTO productOpeningPositionDTO;
    private VerticalLayout layout;

    @PostConstruct
    private void init() {
        layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.setWidthFull();
        layout.addClassName("category-order-card");
    }

    public VerticalLayout getLayout() {
        if (productOpeningPositionDTO == null) {
            throw new RuntimeException("ProductOpeningPostionDTO Dont set");
        }
        return layout;
    }

    public void setProductOpeningPositionDTO(ProductOpeningPositionDTO productOpeningPositionDTO) {
        this.productOpeningPositionDTO = productOpeningPositionDTO;
        constructLayout();
    }

    private void constructLayout() {
        H3 productName = new H3(productOpeningPositionDTO.getProduct().getName());
        layout.add(productName);
        H4 olderOrderReceivedDate = new H4("Tarix: " + productOpeningPositionDTO.getOrderReceivedDate());
        layout.add(olderOrderReceivedDate);

        productOpeningPositionDTO.getOpenedPositions().forEach(position -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();

            TextField orderNoField = new TextField("Sifaris nomresi");
            orderNoField.setValue(position.getOrder().getOrderNo().toString());
            horizontalLayout.add(orderNoField);

            TextField pritedTypeField = new TextField("Prited tipi");
            pritedTypeField.setValue(position.getPrintedType().getName());
            horizontalLayout.add(pritedTypeField);

            layout.add(horizontalLayout);
        });
    }
}
