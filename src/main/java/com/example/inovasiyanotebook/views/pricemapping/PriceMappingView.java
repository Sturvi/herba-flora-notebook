package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;

@PageTitle("PriceList üçün məhsul eyniləşdirilməsi")
@Route(value = "productpricemapping", layout = MainLayout.class)
@RolesAllowed({"EDITOR", "ADMIN"})
@RequiredArgsConstructor
public class PriceMappingView extends VerticalLayout {
    private final DesignTools designTools;
    private final UploaderDialog uploaderDialog;
    private final ProductPriceMapperGrid productPriceMapperGrid;

    @PostConstruct
    private void setupPriceMappingView() {
        setHeightFull();
        setWidthFull();

        var priceMappingPageHeaderLine = designTools.getAllCommonViewHeader("Eyniləşdirmə", null);
        Button uploadButton = new Button("Qiymət siyahısını yüklə");
        uploadButton.addClickListener(event -> {
            uploaderDialog.open();
        });
        priceMappingPageHeaderLine.add(uploadButton, productPriceMapperGrid.getStatusComboBox(), productPriceMapperGrid.getSearchField());
        priceMappingPageHeaderLine.setWidthFull();

        this.add(priceMappingPageHeaderLine);
        add(productPriceMapperGrid);
    }
}
