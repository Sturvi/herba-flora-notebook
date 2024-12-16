package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.ProductExtraInfo;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.ProductRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.FileService;
import com.example.inovasiyanotebook.service.viewservices.product.technicalreview.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements CRUDService<Product> {
    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;


    @Override
    public Product create(Product entity) {
        handleProductNameOrCategoryChange(entity);
        return productRepository.save(entity);
    }

    @Override
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product update(Product entity) {
        handleProductNameOrCategoryChange(entity);

        return productRepository.save(entity);
    }

    private void handleProductNameOrCategoryChange(Product entity) {
        if (entity.getId() != null) {
            productRepository.
                    findById(entity.getId())
                    .ifPresent(existingProduct -> {
                        boolean nameChanged = !existingProduct.getName().equals(entity.getName());
                        boolean categoryChanged = !existingProduct.getCategory().equals(entity.getCategory());

                        if (nameChanged || categoryChanged) {
                            fileUploadService.changeNameOrCategory(
                                    existingProduct.getName(),
                                    entity.getName(),
                                    existingProduct.getCategory().getName(),
                                    entity.getCategory().getName());
                        }
                    });
        }
    }

    @Override
    public void delete(Product entity) {
        productRepository.delete(entity);
    }

    public List<Product> getAllByClient(Client client) {
        return productRepository.findAllByClient(client);
    }

    public List<Product> getAllByCategory(Category category) {
        return productRepository.findAllByCategoryAndHisSubCategory(category);
    }

    @Transactional
    public void updateExtraInfo(Product product, List<ProductExtraInfo> extraInfoList) {
        // Загружаем продукт с полной связанной информацией
        Product existingProduct = productRepository.findByIdWithExtraInfo(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + product.getId()));

        // Удаление лишних записей
        existingProduct.getExtraInfo().removeIf(existingInfo ->
                extraInfoList.stream().noneMatch(newInfo -> newInfo.getKey().equals(existingInfo.getKey()))
        );

        // Добавление новых записей или обновление существующих
        extraInfoList.forEach(newInfo -> {
            ProductExtraInfo existingInfo = existingProduct.getExtraInfo().stream()
                    .filter(info -> info.getKey().equals(newInfo.getKey()))
                    .findFirst()
                    .orElse(null);

            if (existingInfo == null) {
                // Если запись новая, добавляем её
                newInfo.setProduct(existingProduct);
                existingProduct.getExtraInfo().add(newInfo);
            } else {
                // Если запись существует, обновляем её значение
                existingInfo.setValue(newInfo.getValue());
            }
        });

        // Сохранение обновлений
        productRepository.save(existingProduct);

        // Обновление текущего объекта продукта
        product.setExtraInfo(existingProduct.getExtraInfo());
    }


}
