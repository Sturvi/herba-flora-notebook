package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductPriceMappingService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceListHandler {
    private final PriceListParserService parser;
    private final ProductPriceMappingService productPriceMappingService;
    private final ProductService productService;

    public void handlePriceList(MemoryBuffer memoryBuffer) {
        List<PricePositionDTO> positions = parseExcelFile(memoryBuffer);

        // Обрабатываем маппинги в отдельной транзакции
        boolean hasUnmappedPositions = processPricePositions(positions);

        if (!hasUnmappedPositions) {
            updateProductPrices(positions);
        } else {
            throw new PriceListException("Price list not updated - unmapped positions found");
        }
    }

    private List<PricePositionDTO> parseExcelFile(MemoryBuffer memoryBuffer) {
        try {
            return parser.parseExcelFile(memoryBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public boolean processPricePositions(List<PricePositionDTO> positions) {
        boolean hasUnmappedPositions = false;

        for (PricePositionDTO position : positions) {
            ProductPriceMapping mapping = createOrGetMapping(position.getPositionName());

            if (mapping.getProduct() == null) {
                hasUnmappedPositions = true;
            }

            position.setProductPriceMapping(mapping);
        }

        return hasUnmappedPositions;
    }

    private ProductPriceMapping createOrGetMapping(String positionName) {
        var existingMappingOptional = productPriceMappingService.findByIncomingOrderPositionName(positionName);

        if (existingMappingOptional.isPresent()) {
            return existingMappingOptional.get();
        } else {
            var newMapping = new ProductPriceMapping();
            newMapping.setIncomingOrderPositionName(positionName);
            log.trace("Creating new ProductPriceMapping for position: {}", positionName);
            return productPriceMappingService.create(newMapping);
        }
    }

    @Transactional
    public void updateProductPrices(List<PricePositionDTO> positions) {
        for (PricePositionDTO position : positions) {
            var product = position.getProductPriceMapping().getProduct();
            product.setPrice(position.getPrice());
            productService.update(product);
        }
    }
}