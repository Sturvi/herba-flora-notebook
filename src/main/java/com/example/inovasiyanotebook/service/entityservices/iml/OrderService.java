package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.repository.OrderRepository;
import com.example.inovasiyanotebook.repository.specification.OrderSpecifications;
import com.example.inovasiyanotebook.repository.specification.OrderSpecifications.OrderGridFilter;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.entityservices.exceptions.DuplicateOrderException;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport.SortKey;
import com.vaadin.flow.data.provider.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService implements CRUDService<Order> {
    private static final List<SortKey> DEFAULT_GRID_SORT = List.of(SortKey.desc("orderReceivedDate"));

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

    public Set<Order> findOrdersWithClosedPositionsButNonFinalStatus() {
        return orderRepository.findOpenOrdersWithAllPositionsClosedOrCanceled();
    }


    @Override
    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    /**
     * Страница заказов для ленивого грида: фильтр, сортировка из грида (или по умолчанию - по дате получения вниз).
     */
    @Transactional(readOnly = true)
    public List<Order> fetchForGrid(OrderGridFilter filter, Query<Order, ?> query) {
        Specification<Order> specification = OrderSpecifications.forGrid(filter)
                .and(GridQuerySupport.orderBy(GridQuerySupport.toSortKeys(query.getSortOrders(), DEFAULT_GRID_SORT)));
        return orderRepository.findAll(specification, GridQuerySupport.toPageable(query)).getContent();
    }

    @Transactional(readOnly = true)
    public int countForGrid(OrderGridFilter filter) {
        return (int) orderRepository.count(OrderSpecifications.forGrid(filter));
    }

    @Override
    public Order update(Order entity) {
        return orderRepository.save(entity);
    }

    @Override
    public void delete(Order entity) {
        orderRepository.delete(entity);
    }

    public boolean existsByOrderNoAndOrderReceivedDate(Integer orderNo, LocalDate orderReceivedDate) {
        return orderRepository.existsByOrderNoAndOrderReceivedDate(orderNo, orderReceivedDate);
    }

    public Optional<Long> getOrderIdByOrderNoAndOrderReceivedDate(Integer orderNo, LocalDate orderReceivedDate) {
        return orderRepository.findIdByOrderNoAndOrderReceivedDate(orderNo, orderReceivedDate);
    }
}
