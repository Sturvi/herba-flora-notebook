package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.entityservices.iml.RawOrderDataService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.OrderCreationService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrdersScheduledTasks {
    private final RawOrderDataService rawOrderDataService;
    private final ProductMappingService productMappingService;
    private final OrderService orderService;
    private final OrderCreationService orderCreationService;

    @Scheduled(fixedRate = 15000)
    public void processOrders() {
        List<RawOrderData> unprocessedOrders = retrieveUnprocessedOrders();
        processValidOrders(unprocessedOrders);
    }

    private List<RawOrderData> retrieveUnprocessedOrders() {
        return rawOrderDataService.getAllNotProcessed();
    }

    private void processValidOrders(List<RawOrderData> rawOrders) {
        for (RawOrderData rawOrderData : rawOrders) {
            if (isExistingOrder(rawOrderData)) {
                updateRawOrderAsProcessed(rawOrderData);
            } else if (doAllPositionsExist(rawOrderData)) {
                orderCreationService.createAndSaveNewOrder(rawOrderData);
            }
        }
    }

    private boolean isExistingOrder(RawOrderData rawOrderData) {
        return orderService.existsByOrderNoAndOrderReceivedDate(rawOrderData.getOrderNumber(), rawOrderData.getOrderDate());
    }

    private void updateRawOrderAsProcessed(RawOrderData rawOrderData) {
        rawOrderData.setIsProcessed(true);
        rawOrderDataService.update(rawOrderData);
    }

    private boolean doAllPositionsExist(RawOrderData order) {
        List<String> positionNames = order.getPositions()
                .stream()
                .map(RawPositionData::getPositionName)
                .toList();
        return productMappingService.doAllExist(positionNames);
    }
}
