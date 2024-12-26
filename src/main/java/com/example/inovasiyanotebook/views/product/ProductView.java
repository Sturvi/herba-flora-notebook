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
import com.example.inovasiyanotebook.service.viewservices.product.extrainformationdialog.ProductExtraInfoDialogService;
import com.example.inovasiyanotebook.service.viewservices.product.ProductInfoViewService;
import com.example.inovasiyanotebook.service.viewservices.product.ProductsGridService;
import com.example.inovasiyanotebook.service.viewservices.product.technicalreview.FileUploadService;
import com.example.inovasiyanotebook.service.viewservices.product.technicalreview.TechnicalReviewUploader;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import org.hibernate.Hibernate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;


@PageTitle("Məhsul")
@Route(value = "product", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class ProductView extends HorizontalLayout implements HasUrlParameter<String> {
    private final ProductService productService;
    private final ProductInfoViewService infoViewService;
    private final DesignTools designTools;
    private final OrdersGrid ordersGrid;
    private final PermissionsCheck permissionsCheck;
    private final InstructionService instructionService;
    private final NoteGridService noteGridService;
    private final ProductsGridService productsGridService;
    private final TechnicalReviewUploader technicalReviewUploader;
    private final FileUploadService fileUploadService;
    private final ProductExtraInfoDialogService productExtraInfoDialogService;

    private Product product;
    private User user;

    private HorizontalLayout productNameLayout;

    public ProductView(ProductService productService,
                       ProductInfoViewService infoViewService,
                       UserService userService,
                       DesignTools designTools,
                       OrdersGrid ordersGrid,
                       PermissionsCheck permissionsCheck,
                       InstructionService instructionService,
                       NavigationTools navigationTools,
                       NoteGridService noteGridService,
                       ProductsGridService productsGridService,
                       TechnicalReviewUploader technicalReviewUploader,
                       FileUploadService fileUploadService, ProductExtraInfoDialogService productExtraInfoDialogService) {
        this.productService = productService;
        this.infoViewService = infoViewService;
        this.designTools = designTools;
        this.ordersGrid = ordersGrid;
        this.permissionsCheck = permissionsCheck;
        this.instructionService = instructionService;
        this.noteGridService = noteGridService;
        this.productsGridService = productsGridService;
        this.fileUploadService = fileUploadService;
        this.productExtraInfoDialogService = productExtraInfoDialogService;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());

        this.technicalReviewUploader = technicalReviewUploader;

        productNameLayout = new HorizontalLayout();

        setHeightFull();
        setWidthFull();
    }

    @Override
    @Transactional
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String productId) {
        removeAll();

        if (productId == null) {
            allProductsPage();
        } else {
            var productOpt = productService.getById(Long.parseLong(productId));
            productOpt.ifPresentOrElse(product -> {
                        this.product = product;
                        Hibernate.initialize(product.getExtraInfo());
                        handleHasProduct();
                    },
                    this::allProductsPage);
        }
    }

    private void handleHasProduct() {
        handleProductNameLine();
        assembleLayout();
    }

    private void handleProductNameLine() {
        // Clear the layout
        productNameLayout.removeAll();

        // Create a vertical layout to hold two lines
        VerticalLayout verticalLayout = new VerticalLayout();

        // Add product name to the first line
        var productNameLine = infoViewService.getProductNameLine(product);
        verticalLayout.add(productNameLine);

        // Create a layout for additional controls
        HorizontalLayout controlsLayout = new HorizontalLayout();

        // Add instruction button
        Button instruksionButton = new Button("İnstruksiya");
        instruksionButton.addClickListener(buttonClickEvent -> openInstructionDialog(product));
        controlsLayout.add(instruksionButton);

        // Check if document file exists
        var hasDocumentFile = checkIfDocumentFileExist();

        // Add review button if user has contributor permissions or higher
        if (hasDocumentFile && permissionsCheck.isContributorOrHigher()) {
            addReviewButton(hasDocumentFile, controlsLayout);
        }

        // Add technical review uploader for editors or higher
        if (permissionsCheck.isEditorOrHigher()) {
            handleTechnicalReviewUploader(controlsLayout, hasDocumentFile);
        }

        // Add delete document button if document exists and user has editor permissions or higher
        if (hasDocumentFile && permissionsCheck.isEditorOrHigher()) {
            addDeleteDocumentButton(controlsLayout);
        }

        // Add extra information button
        addExtraInformationButton(controlsLayout);

        // Add the controls layout to the second line
        verticalLayout.add(controlsLayout);

        // Add the vertical layout to the productNameLayout
        productNameLayout.add(verticalLayout);
    }


    private void addDeleteDocumentButton(HorizontalLayout hasProductNameLine) {
        Button documentDeleteButton = new Button("Rəyi sil", VaadinIcon.TRASH.create());

        technicalReviewUploader.setUploaderHeaderText("Rəyi yenilə");

        documentDeleteButton.addClickListener((ClickEvent<Button> buttonClickEvent) -> {
            designTools.showConfirmationDialog(() -> {
                try {
                    fileUploadService.deleteFile(product.getName(), product.getCategory().getName());
                    Notification.show("Sənəd uğurla silindi!");
                } catch (Exception e) {
                    Notification.show("Səhv: sənədi silmək mümkün olmadı.");
                }
                handleProductNameLine();
            });
        });

        hasProductNameLine.add(documentDeleteButton);
    }

    private void addExtraInformationButton(HorizontalLayout hasProductNameLine) {
        Button extraInfoButton = new Button("Elave Melumat", VaadinIcon.INFO.create());


        extraInfoButton.addClickListener((ClickEvent<Button> buttonClickEvent) -> {
            productExtraInfoDialogService.openDialog(product);
        });

        hasProductNameLine.add(extraInfoButton);
    }


    private boolean checkIfDocumentFileExist() {
        Boolean hasDocumentFile;
        try {
            hasDocumentFile = Boolean.TRUE.equals(fileUploadService.fileExists(product).getBody());
        } catch (Exception e) {
            hasDocumentFile = false;
            Notification.show(e.getMessage());
        }
        return hasDocumentFile;
    }

    private void addReviewButton(Boolean hasDocumentFile, HorizontalLayout hasProductNameLine) {
        Button button = new Button("Rəy");
        button.addClickListener(buttonClickEvent -> {
            StreamResource resource = createResource(product);
            Anchor anchor = new Anchor(resource, "Open PDF");
            anchor.setTarget("_blank");
            anchor.getStyle().set("display", "none");
            add(anchor); // Добавляем якорь на страницу
            anchor.getElement().callJsFunction("click"); // Симулируем клик по якорю
        });
        hasProductNameLine.add(button);
    }

    private void handleTechnicalReviewUploader(HorizontalLayout hasProductNameLine, Boolean hasDocumentFile) {
        technicalReviewUploader.setProduct(product);

        technicalReviewUploader.setAdditionalFunction(this::handleProductNameLine);

        if (Boolean.TRUE.equals(hasDocumentFile)) {
            technicalReviewUploader.setUploaderHeaderText("Rəyi yenilə");
        }

        hasProductNameLine.add(technicalReviewUploader.getUpload());
    }

    private void assembleLayout() {
        VerticalLayout verticalLayout = new VerticalLayout(
                productNameLayout,
                infoViewService.createProductInformationComponent(product),
                ordersGrid.getOrderGrid(user, product)
        );
        verticalLayout.setWidthFull();
        VerticalLayout notesLayout = new VerticalLayout(
                noteGridService.getVerticalGridWithHeader(product, user)
        );
        notesLayout.setWidthFull();
        add(
                verticalLayout,
                notesLayout
        );
    }

    private StreamResource createResource(Product product) {
        return new StreamResource("review.pdf", () -> {
            ResponseEntity<?> responseEntity = fileUploadService.downloadFile(product);
            if (responseEntity.getBody() instanceof ByteArrayResource) {
                ByteArrayResource byteArrayResource = (ByteArrayResource) responseEntity.getBody();
                try {
                    return byteArrayResource.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        });
    }

    private void openInstructionDialog(Product product) {
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
        if (!permissionsCheck.needEditor()) {
            textArea.setReadOnly(true);
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidthFull(); // Задаем ширину на всю доступную ширину
        buttonsLayout.setJustifyContentMode(JustifyContentMode.END); // Выравнивание содержимого справа

        if (permissionsCheck.needEditor()) {
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
                        infoViewService.getAllProductsHeader(),
                        productsGridService.getProductGrid(user)
                )
        );
    }
}
