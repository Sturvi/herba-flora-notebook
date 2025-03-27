package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.repository.OrderPositionRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.views.openedordersbyproduct.ProductOpeningPositionDTO;
import com.vaadin.flow.component.combobox.ComboBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.inovasiyanotebook.model.order.OrderStatusEnum.*;

@Service
@RequiredArgsConstructor
public class OrderPositionService implements CRUDService<OrderPosition> {

    private final OrderPositionRepository orderPositionRepository;

    public void setOrderPositionStatus(OrderPosition orderPosition, ComboBox<OrderStatusEnum> statusComboBox) {
        switch (statusComboBox.getValue()) {
            case WAITING -> {
                orderPosition.setStatus(WAITING);
                if (orderPosition.getPositionCompletedDateTime() != null) {
                    orderPosition.setPositionCompletedDateTime(null);
                }
            }
            case COMPLETE -> {
                orderPosition.setStatus(COMPLETE);
                if (orderPosition.getPositionCompletedDateTime() == null) {
                    orderPosition.setPositionCompletedDateTime(LocalDateTime.now());
                }
            }
            default -> {
                orderPosition.setStatus(OPEN);
                if (orderPosition.getPositionCompletedDateTime() != null) {
                    orderPosition.setPositionCompletedDateTime(null);
                }
            }
        }
    }

    @Override
    public OrderPosition create(OrderPosition entity) {
        return orderPositionRepository.save(entity);
    }

    @Override
    public Optional<OrderPosition> getById(Long id) {
        return orderPositionRepository.findById(id);
    }

    @Override
    public List<OrderPosition> getAll() {
        return orderPositionRepository.findAll();
    }

    @Override
    public OrderPosition update(OrderPosition entity) {
        return orderPositionRepository.save(entity);
    }

/*    @Override
    public void delete(OrderPosition entity) {
        orderPositionRepository.delete(entity);
    }*/

    public void deleteAll(List<OrderPosition> entities) {
        List<OrderPosition> validEntities = filterEntitiesWithNonNullId(entities);
        orderPositionRepository.deleteAllInBatch(validEntities);
    }

    private List<OrderPosition> filterEntitiesWithNonNullId(List<OrderPosition> entities) {
        return entities.stream()
                .filter(orderPosition -> orderPosition.getId() != null)
                .toList();
    }

    public void delete(OrderPosition entity) {
        orderPositionRepository.deleteAllInBatch(List.of(entity));
    }

    public void saveAll(List<OrderPosition> orderPositions) {
        orderPositionRepository.saveAll(orderPositions);
    }

    public Set<OrderPosition> getAllByProductWithOpenedStatus(Product product) {
        return orderPositionRepository.getAllByProductAndStatus(product, OPEN);
    }

    public List<ProductOpeningPositionDTO> getAllWithOpeningStatusGroupedByProduct() {
        // Получение позиций со статусом OPEN
        List<OrderPosition> openPositions = orderPositionRepository.getAllByStatus(OrderStatusEnum.OPEN);

        Map<Product, ProductOpeningPositionDTO> productOpeningPositionDTOMap = new HashMap<>();

        openPositions
                .stream()
                .filter(position -> position.getOrder().getStatus() == OPEN)
                .forEach(orderPosition -> {
                    if (productOpeningPositionDTOMap.containsKey(orderPosition.getProduct())) {
                        productOpeningPositionDTOMap.get(orderPosition.getProduct()).addOrderPositionIfMatchesProduct(orderPosition);
                    } else {
                        var productOpeningPositionDTO = new ProductOpeningPositionDTO(orderPosition.getProduct());
                        productOpeningPositionDTO.addOrderPositionIfMatchesProduct(orderPosition);
                        productOpeningPositionDTOMap.put(orderPosition.getProduct(), productOpeningPositionDTO);
                    }
                });

        return new ArrayList<>(productOpeningPositionDTOMap.values());
    }
}

