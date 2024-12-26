package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.note.NoteDialog;
import com.example.inovasiyanotebook.service.viewservices.note.NoteGridService;
import com.example.inovasiyanotebook.service.viewservices.order.NewOrderDialog;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

/**
 * Service class representing the layout of product order cards.
 * Сервисный класс, представляющий макет карточек заказов на продукты.
 */
@Slf4j
@Service
@Scope("prototype")
@RequiredArgsConstructor
public class ProductOrderCardLayout {

    private ProductOpeningPositionDTO productOpeningPositionDTO;
    private VerticalLayout layout;

    private final OrderPositionService orderPositionService;
    private final DesignTools designTools;
    private final NoteDialog noteDialog;
    private final UserService userService;
    private final NavigationTools navigationTools;
    private final NewOrderDialog newOrderDialog;
    private final NoteGridService noteGridService;

    /**
     * Initializes the layout after construction.
     * Инициализация макета после создания.
     */
    @PostConstruct
    private void init() {
        log.info("Initializing ProductOrderCardLayout...");
        layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.addClassName("category-order-card");
    }

    /**
     * Retrieves the layout. Throws an exception if DTO is not set.
     * Возвращает макет. Выбрасывает исключение, если DTO не установлено.
     *
     * @return VerticalLayout instance
     */
    public VerticalLayout getLayout() {
        if (productOpeningPositionDTO == null) {
            log.error("ProductOpeningPositionDTO is not set");
            throw new RuntimeException("ProductOpeningPositionDTO is not set");
        }
        return layout;
    }

    /**
     * Sets the ProductOpeningPositionDTO and constructs the layout.
     * Устанавливает ProductOpeningPositionDTO и создает макет.
     *
     * @param productOpeningPositionDTO DTO containing product data
     */
    public void setProductOpeningPositionDTO(ProductOpeningPositionDTO productOpeningPositionDTO) {
        this.productOpeningPositionDTO = productOpeningPositionDTO;
        log.info("ProductOpeningPositionDTO set. Constructing layout...");
        constructLayout();
    }

    /**
     * Constructs the layout using the DTO data.
     * Создает макет, используя данные из DTO.
     */
    private void constructLayout() {
        layout.removeAll(); // Clear existing components to prevent duplication

        Span productNameSpan = new Span(productOpeningPositionDTO.getProduct().getName());
        productNameSpan.setClassName("product-label");
        layout.add(productNameSpan);

        Span dateLabel = new Span("Ən köhnə sifariş tarixi: " + productOpeningPositionDTO.getEarliestOrderReceivedDate());
        dateLabel.addClassName("product-date-label");
        layout.add(dateLabel);

        productOpeningPositionDTO.getOpenOrderPositions().forEach(position -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

            horizontalLayout.add(getTextFieldWithCustomSize("Sifariş №", position.getOrder().getOrderNo().toString(), "80px"));
            horizontalLayout.add(getTextFieldWithCustomSize("Çap növü", position.getPrintedType().getName(), "140px"));
            horizontalLayout.add(getTextFieldWithCustomSize("Say", position.getCount(), "140px"));
            horizontalLayout.add(designTools.createTextFieldWithValue("Not", position.getComment(), true));
            horizontalLayout.add(getOrderStatusEnumComboBox(position));

            horizontalLayout.add(designTools.getNewIconButton(VaadinIcon.NOTEBOOK.create(), () -> {
                noteDialog.openDialog(position.getProduct(), userService.findByUsername(navigationTools.getCurrentUsername()));
            }));
            horizontalLayout.add(designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> {
                newOrderDialog.openNewDialog(position.getOrder());
            }));


            layout.add(horizontalLayout,  noteGridService.getNoteGrid(position.getOrder(), userService.findByUsername(navigationTools.getCurrentUsername())));
        });

        log.info("Layout constructed successfully.");
    }

    private TextField getTextFieldWithCustomSize (String label, String value, String size) {
        var textField = designTools.createTextFieldWithValue(label, value, true);
        textField.setMaxWidth(size);

        return textField;
    }

    private ComboBox<OrderStatusEnum> getOrderStatusEnumComboBox(OrderPosition position) {
        ComboBox<OrderStatusEnum> statusComboBox = designTools.creatComboBox("Status", List.of(OrderStatusEnum.values()), OrderStatusEnum::getName);
        statusComboBox.setValue(position.getStatus());
        statusComboBox.addValueChangeListener(event -> {
            position.setStatus(statusComboBox.getValue());
            orderPositionService.update(position);
        });
        statusComboBox.setMaxWidth("140px");
        return statusComboBox;
    }


}
