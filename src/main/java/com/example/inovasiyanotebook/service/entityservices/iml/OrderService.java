package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.repository.OrderRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.entityservices.exceptions.DuplicateOrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService implements CRUDService<Order> {
    private final OrderRepository orderRepository;
    private final ProductMappingService productMappingService;

    @Override
    public Order create(Order entity) {
        checkOrderRelevance(entity);
        return orderRepository.save(entity);
    }

    public void checkOrderRelevance(Order entity) {
        if (orderRepository.existsByOrderNoAndOrderReceivedDate(entity.getOrderNo(), entity.getOrderReceivedDate())) {
            throw new DuplicateOrderException("Order with the same orderNo and orderReceivedDate already exists");
        }
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

    public Set<Order> getAllByProduct(Product product){
        return orderRepository.findAllByProducts(product);
    }

    public boolean existsByOrderNoAndOrderReceivedDate(Integer orderNo, LocalDate orderReceivedDate) {
        return orderRepository.existsByOrderNoAndOrderReceivedDate(orderNo, orderReceivedDate);
    }

    public Optional<Long> getOrderIdByOrderNoAndOrderReceivedDate(Integer orderNo, LocalDate orderReceivedDate) {
        return orderRepository.findIdByOrderNoAndOrderReceivedDate(orderNo, orderReceivedDate);
    }
}
