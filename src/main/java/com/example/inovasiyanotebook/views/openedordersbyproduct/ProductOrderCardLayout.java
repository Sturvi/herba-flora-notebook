package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.textfield.TextField;

/**
 * Service class representing the layout of product order cards.
 * Сервисный класс, представляющий макет карточек заказов на продукты.
 */
@Slf4j
@Service
@Scope("prototype")
public class ProductOrderCardLayout {

    private ProductOpeningPositionDTO productOpeningPositionDTO;
    private VerticalLayout layout;

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

        H3 productName = new H3(productOpeningPositionDTO.getProduct().getName());
        layout.add(productName);

        H4 oldestOrderReceivedDate = new H4("Date: " + productOpeningPositionDTO.getEarliestOrderReceivedDate());
        layout.add(oldestOrderReceivedDate);

        productOpeningPositionDTO.getOpenOrderPositions().forEach(position -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();

            TextField orderNoField = new TextField("Order Number");
            orderNoField.setValue(position.getOrder().getOrderNo().toString());
            orderNoField.setReadOnly(true);
            horizontalLayout.add(orderNoField);

            TextField printedTypeField = new TextField("Printed Type");
            printedTypeField.setValue(position.getPrintedType().getName());
            printedTypeField.setReadOnly(true);
            horizontalLayout.add(printedTypeField);

            layout.add(horizontalLayout);
        });

        log.info("Layout constructed successfully.");
    }
}
