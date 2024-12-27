package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.entityservices.iml.RawOrderDataService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.OrderCreationService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled tasks for processing orders.
 * Запланированные задачи для обработки заказов.
 */
@Component
@RequiredArgsConstructor
public class OrdersScheduledTasks {
    private final RawOrderDataService rawOrderDataService;
    private final ProductMappingService productMappingService;
    private final OrderService orderService;
    private final OrderCreationService orderCreationService;

    /**
     * Processes unprocessed orders every 15 seconds.
     * Обрабатывает необработанные заказы каждые 15 секунд.
     */
    @Scheduled(fixedRate = 15000)
    public void processOrders() {
        List<RawOrderData> unprocessedOrders = retrieveUnprocessedOrders();
        processValidOrders(unprocessedOrders);
    }

    /**
     * Finds and closes orders with closed positions but non-final status every 10 seconds.
     * Находит и закрывает заказы с закрытыми позициями, но не в конечном статусе, каждые 10 секунд.
     */
    @Scheduled(fixedRate = 10000)
    private void findAndCloseOrders() {
        orderService.findOrdersWithClosedPositionsButNonFinalStatus()
                .stream()
                .peek(order -> {
                    order.setStatus(OrderStatusEnum.COMPLETE);
                    order.setOrderCompletedDateTime(LocalDateTime.now());
                })
                .forEach(orderService::update);
    }

    /**
     * Retrieves all unprocessed orders.
     * Запрашивает все необработанные заказы.
     *
     * @return List of unprocessed orders.
     * Список необработанных заказов.
     */
    private List<RawOrderData> retrieveUnprocessedOrders() {
        return rawOrderDataService.getAllNotProcessed();
    }

    /**
     * Processes valid orders by either marking them as processed or creating new ones.
     * Обработывает действительные заказы, обозначая их как обработанные или создавая новые.
     *
     * @param rawOrders List of raw orders to process.
     * Список сырых заказов для обработки.
     */
    private void processValidOrders(List<RawOrderData> rawOrders) {
        for (RawOrderData rawOrderData : rawOrders) {
            if (isExistingOrder(rawOrderData)) {
                updateRawOrderAsProcessed(rawOrderData);
            } else if (doAllPositionsExist(rawOrderData)) {
                orderCreationService.createAndSaveNewOrder(rawOrderData);
            }
        }
    }

    /**
     * Checks if an order already exists based on its number and date.
     * Проверяет, существует ли заказ на основе его номера и даты.
     *
     * @param rawOrderData Raw order data to check.
     * Сырые данные заказа для проверки.
     * @return True if the order exists, false otherwise.
     * True, если заказ существует, false иначе.
     */
    private boolean isExistingOrder(RawOrderData rawOrderData) {
        return orderService.existsByOrderNoAndOrderReceivedDate(rawOrderData.getOrderNumber(), rawOrderData.getOrderDate());
    }

    /**
     * Marks a raw order as processed.
     * Помечает сырой заказ как обработанный.
     *
     * @param rawOrderData Raw order data to mark as processed.
     * Сырые данные заказа для пометки.
     */
    private void updateRawOrderAsProcessed(RawOrderData rawOrderData) {
        rawOrderData.setIsProcessed(true);
        rawOrderDataService.update(rawOrderData);
    }

    /**
     * Checks if all positions in the order exist in the product mapping.
     * Проверяет, все ли позиции заказа существуют в соответствии товаров.
     *
     * @param order Raw order data containing positions to check.
     * Сырой заказ, содержащий позиции для проверки.
     * @return True if all positions exist, false otherwise.
     * True, если все позиции существуют, false иначе.
     */
    private boolean doAllPositionsExist(RawOrderData order) {
        List<String> positionNames = order.getPositions()
                .stream()
                .map(RawPositionData::getPositionName)
                .toList();
        return productMappingService.doAllExist(positionNames);
    }
}
