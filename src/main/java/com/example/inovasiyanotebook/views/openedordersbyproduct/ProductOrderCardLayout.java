package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.dto.ProductOpenInfoDTO;
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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDateTime;
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

    private ProductOpenInfoDTO productOpenInfoDTO;
    private VerticalLayout layout;

    private final OrderPositionService orderPositionService;
    private final DesignTools designTools;
    private final NoteDialog noteDialog;
    private final UserService userService;
    private final NavigationTools navigationTools;
    private final NewOrderDialog newOrderDialog;
    private final NoteGridService noteGridService;
    private final ObjectProvider<ChangeTasksLayout> layoutProvider;

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
        if (productOpenInfoDTO == null) {
            log.error("ProductOpeningPositionDTO is not set");
            throw new RuntimeException("ProductOpeningPositionDTO is not set");
        }
        return layout;
    }

    /**
     * Sets the ProductOpeningPositionDTO and constructs the layout.
     * Устанавливает ProductOpeningPositionDTO и создает макет.
     *
     * @param productOpenInfoDTO DTO containing product data
     */
    public void setProductOpenInfoDTO(ProductOpenInfoDTO productOpenInfoDTO) {
        this.productOpenInfoDTO = productOpenInfoDTO;
        log.info("ProductOpeningPositionDTO set. Constructing layout...");
        constructLayout();
    }

    /**
     * Constructs the layout using the DTO data.
     * Создает макет, используя данные из DTO.
     */
    private void constructLayout() {
        layout.removeAll(); // Clear existing components to prevent duplication

        Span productNameSpan = new Span(productOpenInfoDTO.getProduct().getName());
        productNameSpan.setClassName("product-label");
        layout.add(productNameSpan);

        Span dateLabel = new Span("Ən köhnə sifariş tarixi: " + productOpenInfoDTO.getEarliestOrderReceivedDate());
        dateLabel.addClassName("product-date-label");
        layout.add(dateLabel);


        var positions = productOpenInfoDTO.getOpenOrderPositions();

        for (int i = 0; i < positions.size(); i++) {
            final int index = i; // Переменная должна быть effectively final для использования в лямбда-выражении

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

            // Добавление текстовых полей для отображения данных позиции
            horizontalLayout.add(getTextFieldWithCustomSize("Sifariş №", positions.get(index).getOrder().getOrderNo().toString(), "80px"));
            horizontalLayout.add(getTextFieldWithCustomSize("Çap növü", positions.get(index).getPrintedType().getName(), "140px"));
            horizontalLayout.add(getTextFieldWithCustomSize("Say", positions.get(index).getCount(), "140px"));
            horizontalLayout.add(designTools.createTextFieldWithValue("Not", positions.get(index).getComment(), true));
            horizontalLayout.add(getOrderStatusEnumComboBox(positions.get(index)));

            // Добавление кнопки для открытия диалога заметок
            horizontalLayout.add(designTools.getNewIconButton(VaadinIcon.NOTEBOOK.create(), () -> {
                noteDialog.openDialog(positions.get(index).getProduct(), userService.findByUsername(navigationTools.getCurrentUsername()));
            }));

            // Добавление кнопки для редактирования заказа
            horizontalLayout.add(designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> {
                newOrderDialog.openNewDialog(positions.get(index).getOrder());
            }));

            // Добавление горизонтальной компоновки в основной лейаут
            layout.add(horizontalLayout);

            // Добавление сетки заметок для последней позиции
            if (index == positions.size() - 1) {
                layout.add(noteGridService.getHorizontalGridWithHeader(positions.get(index).getProduct()));
            }

        }

        if (!productOpenInfoDTO.getOpenChangeTaskItems().isEmpty()){
            var changeTasksLayout = layoutProvider.getObject();
            changeTasksLayout.setChangeTaskItems(productOpenInfoDTO.getOpenChangeTaskItems());
            layout.add(changeTasksLayout.getLayout());
        }

        log.info("Layout constructed successfully.");
    }

    private TextField getTextFieldWithCustomSize(String label, String value, String size) {
        var textField = designTools.createTextFieldWithValue(label, value, true);
        textField.setMaxWidth(size);

        return textField;
    }


    private ComboBox<OrderStatusEnum> getOrderStatusEnumComboBox(OrderPosition position) {
        ComboBox<OrderStatusEnum> statusComboBox = designTools.creatComboBox("Status", List.of(OrderStatusEnum.values()), OrderStatusEnum::getName);
        statusComboBox.setValue(position.getStatus());
        statusComboBox.addValueChangeListener(event -> {
            position.setPositionCompletedDateTime(LocalDateTime.now());
            position.setStatus(statusComboBox.getValue());
            orderPositionService.update(position);
        });
        statusComboBox.setMaxWidth("140px");
        return statusComboBox;
    }


}
