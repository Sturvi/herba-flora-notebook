package com.example.inovasiyanotebook.service.viewservices.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderComponentsFactory {
    private final ApplicationContext context;

    public OrderComponents getNewBean () {
        return context.getBean(OrderComponents.class);
    }
}
