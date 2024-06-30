package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.viewservices.order.NewOrderDialog;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@UIScope
public class OrderCreationService {

    private final NewOrderDialog newOrderDialog;
    private final ProductMappingService productMappingService;

    /**
     * Создает новый заказ на основе предоставленных данных и открывает диалоговое окно для нового заказа.
     *
     * @param rawOrderData данные о заказе.
     */
    public void createNewOrder(RawOrderData rawOrderData) {
        log.trace("Начало создания нового заказа: {}", rawOrderData);

        Order order = createOrder(rawOrderData);
        log.debug("Создан объект заказа: {}", order);

        List<OrderPosition> orderPositions = createOrderPositions(rawOrderData.getPositions(), order);
        log.debug("Созданы позиции заказа: {}", orderPositions);

        if (orderPositions.isEmpty()) {
            log.error("В заказе отсутствуют позиции.");
            throw new IllegalStateException("No positions in the order.");
        }

        order.setOrderPositions(orderPositions);
        log.trace("Позиции добавлены в заказ.");

        newOrderDialog.openNewDialog(order);
        log.trace("Открыто диалоговое окно для нового заказа.");
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
        order.setOrderReceivedDateTime(LocalDateTime.of(rawOrderData.getOrderDate(), LocalTime.now()));
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
