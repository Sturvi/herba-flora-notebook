package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.service.viewservices.order.OrderComponents;
import com.example.inovasiyanotebook.service.viewservices.ordermapping.ProductMappingDialog;
import com.example.inovasiyanotebook.views.pricemapping.PricePositionMapperDialog;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrototypeComponentsFactory {
    private final ApplicationContext context;

    public OrderComponents getOrderComponents() {
        return context.getBean(OrderComponents.class);
    }

    public ProductMappingDialog getProductMappingDialogComponent() {
        return context.getBean(ProductMappingDialog.class);
    }

    public PricePositionMapperDialog getPricePositionMapperDialog() {
        return context.getBean(PricePositionMapperDialog.class);
    }
}
