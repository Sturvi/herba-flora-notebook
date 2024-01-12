package com.example.inovasiyanotebook.views.product;


import com.example.inovasiyanotebook.model.Instruction;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.InstructionService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.note.NoteGridService;
import com.example.inovasiyanotebook.service.viewservices.order.OrdersGrid;
import com.example.inovasiyanotebook.service.viewservices.product.ProductInfoViewService;
import com.example.inovasiyanotebook.service.viewservices.product.ProductsGridService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;


@PageTitle("Məhsul")
@Route(value = "product", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class ProductView extends HorizontalLayout implements HasUrlParameter<String> {
    private final ProductService productService;
    private final ProductInfoViewService infoViewService;
    private final UserService userService;
    private final DesignTools designTools;
    private final OrdersGrid ordersGrid;
    private final PermissionsCheck permissionsCheck;
    private final InstructionService instructionService;
    private final NavigationTools navigationTools;
    private final NoteGridService noteGridService;
    private final ProductsGridService productsGridService;

    private Product product;
    private User user;

    public ProductView(ProductService productService, ProductInfoViewService infoViewService, UserService userService, DesignTools designTools, OrdersGrid ordersGrid, PermissionsCheck permissionsCheck, InstructionService instructionService, NavigationTools navigationTools, NoteGridService noteGridService, ProductsGridService productsGridService) {
        this.productService = productService;
        this.infoViewService = infoViewService;
        this.userService = userService;
        this.designTools = designTools;
        this.ordersGrid = ordersGrid;
        this.permissionsCheck = permissionsCheck;
        this.instructionService = instructionService;
        this.navigationTools = navigationTools;
        this.noteGridService = noteGridService;
        this.productsGridService = productsGridService;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());

        setHeightFull();
        setWidthFull();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String productId) {
        removeAll();

        if (productId == null) {
            allProductsPage();
        } else {
            var productOpt = productService.getById(Long.parseLong(productId));
            productOpt.ifPresentOrElse(product -> {
                        this.product = product;
                        handleHasProduct();
                    },
                    this::allProductsPage);
        }
    }

    private void handleHasProduct() {

        HorizontalLayout hasProductNameLine = infoViewService.getProductNameLine(product, user);
        Button instruksionButton = new Button("İnstruksiya");
        instruksionButton.addClickListener(buttonClickEvent -> {
            openInstructionDialog(product, user);
        });
        hasProductNameLine.add(instruksionButton);

        VerticalLayout verticalLayout = new VerticalLayout(
                hasProductNameLine,
                infoViewService.getProductInformation(product, user),
                ordersGrid.getOrderGrid(user, product)
        );
        verticalLayout.setWidthFull();

        VerticalLayout notesLayout = new VerticalLayout(
                noteGridService.getNoteGrid(product, user)
        );
        notesLayout.setWidthFull();

        add(
                verticalLayout,
                notesLayout
        );
    }

    private void openInstructionDialog(Product product, User user) {
        Dialog dialog = new Dialog();
        dialog.setHeightFull();
        dialog.setMinWidth("500px");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Optional<Instruction> instructionOpt = instructionService.findByProduct(product);
        Instruction instruction = instructionOpt.orElse(Instruction.builder().product(product).build());
        TextArea textArea = new TextArea("İnstruksiya");
        textArea.setHeightFull();
        textArea.setWidthFull();
        if (instruction.getText() != null) {
            textArea.setValue(instruction.getText());
        }
        if (!permissionsCheck.needEditor(user)) {
            textArea.setReadOnly(true);
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidthFull(); // Задаем ширину на всю доступную ширину
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END); // Выравнивание содержимого справа

        if (permissionsCheck.needEditor(user)) {
            Button saveButton = designTools.getNewIconButton(new Icon("lumo", "edit"), () -> {
                instruction.setText(textArea.getValue());
                saveInstuction(instruction);
                dialog.close();
            });
            saveButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            buttonsLayout.add(saveButton);
        }

        Button closeButton = designTools.getNewIconButton(new Icon("lumo", "cross"), dialog::close);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // Добавляем кнопки к layout
        buttonsLayout.add(closeButton);
        dialog.getHeader().add(buttonsLayout);

        dialog.add(textArea);
        dialog.setHeightFull();
        dialog.open();
    }

    private void saveInstuction(Instruction instruction) {
        instructionService.update(instruction);
    }

    private void allProductsPage() {
        add(
                new VerticalLayout(
                        infoViewService.getAllProductsHeader(user),
                        productsGridService.getProductGrid(user)
                )
        );
    }
}
