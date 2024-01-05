package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.repository.OrderRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements CRUDService<Order> {
    private final OrderRepository orderRepository;

    @Override
    public Order create(Order entity) {
        return orderRepository.save(entity);
    }

    @Override
    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order update(Order entity) {
        return orderRepository.save(entity);
    }

    @Override
    public void delete(Order entity) {
        orderRepository.delete(entity);
    }
}
