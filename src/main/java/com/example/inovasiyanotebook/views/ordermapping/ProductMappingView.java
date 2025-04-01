package com.example.inovasiyanotebook.views.ordermapping;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.ordermapping.OrderMappingStatusEnum;
import com.example.inovasiyanotebook.service.viewservices.ordermapping.ProductMappingGridService;
import com.example.inovasiyanotebook.service.viewservices.product.AddNewProductViewService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.atmosphere.config.service.Post;

@PageTitle("Sifariş məhsulları")
@Route(value = "productmapping", layout = MainLayout.class)
@RolesAllowed({"EDITOR"})
@RequiredArgsConstructor
public class ProductMappingView extends VerticalLayout {
    private final DesignTools designTools;
    private final NavigationTools navigationTools;
    private final ProductMappingGridService productMappingGridService;
    private final AddNewProductViewService addNewProductViewService;

    @PostConstruct
    private void setupProductMappingView(){
        setHeightFull();
        setWidthFull();

        var productMappingPageHeaderLine = designTools.getAllCommonViewHeader("Eyniləşdirmə", null);
        productMappingPageHeaderLine.setWidthFull();
        productMappingPageHeaderLine.add(addNewProductButtonOnClick(addNewProductViewService));
        productMappingPageHeaderLine.add(getStatusComboBox());

        var productMappingGrid = productMappingGridService.getOrderMappingGridLayout();

        var searchField = getSearchField();
        searchField.addValueChangeListener(event -> productMappingGridService.setSearchTerm(searchField.getValue()));

        productMappingPageHeaderLine.add(searchField);

        add(productMappingPageHeaderLine, productMappingGrid);
    }

    private Button addNewProductButtonOnClick(AddNewProductViewService addNewProductViewService) {
        Button button = new Button("Məhsul əlavə et");
        button.addClickListener(buttonClickEvent -> addNewProductViewService.creatNewProductDialog());
        return button;
    }

    private ComboBox<OrderMappingStatusEnum> getStatusComboBox () {
        ComboBox<OrderMappingStatusEnum> comboBox = new ComboBox<>();
        comboBox.setItems(OrderMappingStatusEnum.values());
        comboBox.setItemLabelGenerator(OrderMappingStatusEnum::getDisplayName);
        comboBox.addValueChangeListener(event -> productMappingGridService.setFilter(event.getValue()));
        comboBox.setValue(OrderMappingStatusEnum.TO_BE_MAPPED);
        return comboBox;
    }

    private TextField getSearchField() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setWidthFull();
        return searchField;
    }
}
