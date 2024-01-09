package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.repository.OrderPositionRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderPositionService implements CRUDService<OrderPosition> {

    private final OrderPositionRepository orderPositionRepository;

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
        orderPositionRepository.deleteAllInBatch(entities);
    }

    public void delete(OrderPosition entity) {
        orderPositionRepository.deleteAllInBatch(List.of(entity));
    }

    public void saveAll(List<OrderPosition> orderPositions) {
        orderPositionRepository.saveAll(orderPositions);
    }
}
