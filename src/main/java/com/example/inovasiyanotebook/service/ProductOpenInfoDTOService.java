package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.dto.ProductOpenInfoDTO;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskItemService;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductOpenInfoDTOService {
    private final OrderPositionService orderPositionService;
    private final ChangeTaskItemService changeTaskItemService;


    public List<ProductOpenInfoDTO> getProductOpenInfo() {
        // Получаем все открытые позиции заказов
        List<OrderPosition> openOrderPositions = orderPositionService.getAllByStatus(OrderStatusEnum.OPEN);

        // Получаем все открытые задачи на изменение (например, со статусом PENDING)
        List<ChangeTaskItem> openChangeTaskItems = changeTaskItemService.findAllByStatus(ChangeItemStatus.PENDING);

        // Группируем позиции заказов по продукту
        Map<Product, List<OrderPosition>> orderPositionsGrouped = openOrderPositions.stream()
                .collect(Collectors.groupingBy(OrderPosition::getProduct));

        // Группируем задачи на изменение по продукту
        Map<Product, List<ChangeTaskItem>> changeTasksGrouped = openChangeTaskItems.stream()
                .collect(Collectors.groupingBy(ChangeTaskItem::getProduct));

        List<ProductOpenInfoDTO> dtoList = new ArrayList<>();

        // Для каждого продукта формируем DTO
        for (Map.Entry<Product, List<OrderPosition>> entry : orderPositionsGrouped.entrySet()) {
            Product product = entry.getKey();
            List<OrderPosition> positions = entry.getValue();

            // Вычисляем минимальную дату получения заказа
            LocalDate earliestDate = positions.stream()
                    .map(op -> op.getOrder().getOrderReceivedDate())
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());

            // Определяем родительскую категорию
            Category parentCategory = product.getCategory();
            while (parentCategory.hasParent()) {
                parentCategory = parentCategory.getParentCategory();
            }

            // Получаем список задач для продукта, если они есть
            List<ChangeTaskItem> taskItems = changeTasksGrouped.getOrDefault(product, Collections.emptyList());

            dtoList.add(new ProductOpenInfoDTO(product, positions, taskItems, earliestDate, parentCategory));
        }

        return dtoList;
    }
}
