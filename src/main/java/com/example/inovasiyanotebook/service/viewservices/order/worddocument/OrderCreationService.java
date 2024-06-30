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

    public void createNewOrder(RawOrderData rawOrderData) {
        Order order = createOrder(rawOrderData);
        List<OrderPosition> orderPositions = createOrderPositions(rawOrderData.getPositions(), order);

        if (orderPositions.isEmpty()) {
            throw new IllegalStateException("No positions in the order.");
        }

        order.setOrderPositions(orderPositions);
        newOrderDialog.openNewDialog(order);
    }

    private Order createOrder(RawOrderData rawOrderData) {
        Order order = new Order();
        order.setOrderNo(rawOrderData.getOrderNumber());
        order.setOrderReceivedDateTime(LocalDateTime.of(rawOrderData.getOrderDate(), LocalTime.now()));
        order.setStatus(OrderStatusEnum.OPEN);
        return order;
    }

    private List<OrderPosition> createOrderPositions(List<RawPositionData> documentOrderPositions, Order order) {
        return documentOrderPositions.stream()
                .flatMap(documentOrderPosition -> toOrderPosition(documentOrderPosition, order).stream())
                .toList();
    }

    private Optional<OrderPosition> toOrderPosition(RawPositionData documentOrderPosition, Order order) {
        return productMappingService.findByIncomingOrderPositionName(documentOrderPosition.getPositionName())
                .map(productMapping -> OrderPosition.builder()
                        .order(order)
                        .product(productMapping.getProduct())
                        .printedType(productMapping.getPrintedType())
                        .status(OrderStatusEnum.OPEN)
                        .count(documentOrderPosition.getQuantity().toString())
                        .comment(productMapping.getComment() + " " + documentOrderPosition.getShelfLife() + " " + documentOrderPosition.getNote())
                        .build());
    }
}

