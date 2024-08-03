package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.viewservices.order.NewOrderDialog;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class OrderCreationService {

    private final NewOrderDialog newOrderDialog;
    private final ProductMappingService productMappingService;
    private final OrderService orderService;

    /**
     * Creates a new order based on the given data and opens a dialog for the new order.
     *
     * @param rawOrderData order data.
     */
    public void createAndOpenDialogForNewOrder(RawOrderData rawOrderData) {
        Order order = createAndAssignOrderPositions(rawOrderData);
        newOrderDialog.openNewDialog(order);
        log.trace("Opened a dialog for the new order.");
    }

    /**
     * Creates a new order based on the given data and saves the order.
     *
     * @param rawOrderData order data.
     */
    public void createAndSaveNewOrder(RawOrderData rawOrderData) {
        Order order = createAndAssignOrderPositions(rawOrderData);
        orderService.create(order); // assuming you have an OrderService with a save method.
        log.debug("The order has been saved: {}", order);
    }

    /**
     * Helper method to create an order and assign positions to it.
     *
     * @param rawOrderData order data.
     * @return the created order
     */
    private Order createAndAssignOrderPositions(RawOrderData rawOrderData) {
        log.trace("Start of creating a new order: {}", rawOrderData);

        Order order = createOrder(rawOrderData);
        log.debug("Created order object: {}", order);

        List<OrderPosition> orderPositions = createOrderPositions(rawOrderData.getPositions(), order);
        log.debug("Created order positions: {}", orderPositions);

        if (orderPositions.isEmpty()) {
            log.error("No positions in the order.");
            throw new IllegalStateException("No positions in the order.");
        }

        order.setOrderPositions(orderPositions);
        log.trace("Positions have been added to the order.");

        return order;
    }

    /**
     * Создает объект заказа на основе предоставленных данных.
     *
     * @param rawOrderData данные о заказе.
     * @return объект заказа.
     */
    private Order createOrder(RawOrderData rawOrderData) {
        log.trace("Начало создания объекта заказа из данных: {}", rawOrderData);

        Order order = new Order();
        order.setOrderNo(rawOrderData.getOrderNumber());
        order.setOrderReceivedDate(rawOrderData.getOrderDate());
        order.setStatus(OrderStatusEnum.OPEN);

        log.trace("Создан объект заказа: {}", order);
        return order;
    }

    /**
     * Создает список позиций заказа на основе предоставленных данных.
     *
     * @param documentOrderPositions список данных о позициях заказа.
     * @param order объект заказа, к которому относятся позиции.
     * @return список позиций заказа.
     */
    private List<OrderPosition> createOrderPositions(List<RawPositionData> documentOrderPositions, Order order) {
        log.trace("Начало создания позиций заказа из данных: {}", documentOrderPositions);

        List<OrderPosition> orderPositions = documentOrderPositions.stream()
                .flatMap(documentOrderPosition -> toOrderPosition(documentOrderPosition, order).stream())
                .toList();

        log.trace("Созданы позиции заказа: {}", orderPositions);
        return orderPositions;
    }

    /**
     * Преобразует данные о позиции заказа в объект позиции заказа.
     *
     * @param documentOrderPosition данные о позиции заказа.
     * @param order объект заказа, к которому относится позиция.
     * @return Optional с объектом позиции заказа, если найдена сопоставленная продукция.
     */
    private Optional<OrderPosition> toOrderPosition(RawPositionData documentOrderPosition, Order order) {
        log.trace("Начало преобразования данных о позиции заказа в объект позиции заказа: {}", documentOrderPosition);

        Optional<OrderPosition> orderPosition = productMappingService.findByIncomingOrderPositionName(documentOrderPosition.getPositionName())
                .map(productMapping -> OrderPosition.builder()
                        .order(order)
                        .product(productMapping.getProduct())
                        .printedType(productMapping.getPrintedType())
                        .status(OrderStatusEnum.OPEN)
                        .count(documentOrderPosition.getQuantity().toString())
                        .comment(productMapping.getComment() + " " + documentOrderPosition.getShelfLife() + " " + documentOrderPosition.getNote())
                        .build());

        log.trace("Преобразование данных о позиции заказа завершено: {}", orderPosition);
        return orderPosition;
    }
}
