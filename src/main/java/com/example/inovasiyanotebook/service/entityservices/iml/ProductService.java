package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.ProductExtraInfo;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.ProductRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.viewservices.product.technicalreview.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing Product entities. Provides CRUD operations and additional functionality.
 * Сервис для управления сущностями Product. Обеспечивает операции CRUD и дополнительный функционал.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements CRUDService<Product> {

    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;

    /**
     * Creates a new product entity.
     * Создает новую сущность продукта.
     *
     * @param entity the product to be created. / продукт, который нужно создать.
     * @return the saved product entity. / сохраненная сущность продукта.
     */
    @Override
    public Product create(Product entity) {
        log.info("Creating a new product: {}", entity);
        handleProductNameOrCategoryChange(entity);
        return productRepository.save(entity);
    }

    /**
     * Retrieves a product by its ID.
     * Получает продукт по его ID.
     *
     * @param id the ID of the product. / ID продукта.
     * @return an Optional containing the product if found, or empty if not found. / Optional с продуктом, если он найден, или пустой, если не найден.
     */
    @Override
    public Optional<Product> getById(Long id) {
        log.debug("Retrieving product by ID: {}", id);
        return productRepository.findById(id);
    }

    /**
     * Retrieves all products.
     * Получает все продукты.
     *
     * @return a list of all products. / список всех продуктов.
     */
    @Override
    @Transactional
    public List<Product> getAll() {
        log.debug("Retrieving all products.");
        return productRepository.findAll();
    }

    @Transactional
    public List<Product> getAllWithCategory() {
        var products = getAll();
        products.forEach(product -> Hibernate.initialize(product.getCategory()));

        return products;
    }

    public List<Product> getAllHerbaFloraProduct() {
        var products = productRepository.findAllByClientNameIgnoreCase("Herba Flora");
        log.info("Retrieving all herba flora products by name: {}", products);
        return products;
    }

    /**
     * Updates an existing product.
     * Обновляет существующий продукт.
     *
     * @param entity the product with updated information. / продукт с обновленной информацией.
     * @return the updated product entity. / обновленная сущность продукта.
     */
    @Override
    public Product update(Product entity) {
        log.info("Updating product: {}", entity);
        handleProductNameOrCategoryChange(entity);
        return productRepository.save(entity);
    }

    /**
     * Handles changes to the product's name or category.
     * Обрабатывает изменения имени или категории продукта.
     *
     * @param entity the product being updated or created. / продукт, который обновляется или создается.
     */
    private void handleProductNameOrCategoryChange(Product entity) {
        if (entity.getId() != null) {
            log.debug("Checking for name or category changes for product ID: {}", entity.getId());
            productRepository.findById(entity.getId())
                    .ifPresent(existingProduct -> {
                        boolean nameChanged = !existingProduct.getName().equals(entity.getName());
                        boolean categoryChanged = !existingProduct.getCategory().equals(entity.getCategory());

                        if (nameChanged || categoryChanged) {
                            log.info("Product name or category changed. Old name: '{}', New name: '{}', Old category: '{}', New category: '{}'",
                                    existingProduct.getName(), entity.getName(),
                                    existingProduct.getCategory().getName(), entity.getCategory().getName());
                            fileUploadService.changeNameOrCategory(
                                    existingProduct.getName(),
                                    entity.getName(),
                                    existingProduct.getCategory().getName(),
                                    entity.getCategory().getName());
                        }
                    });
        }
    }

    /**
     * Deletes a product.
     * Удаляет продукт.
     *
     * @param entity the product to delete. / продукт, который нужно удалить.
     */
    @Override
    public void delete(Product entity) {
        log.warn("Deleting product: {}", entity);
        productRepository.delete(entity);
    }

    /**
     * Retrieves all products associated with a specific client.
     * Получает все продукты, связанные с определенным клиентом.
     *
     * @param client the client whose products are to be retrieved. / клиент, чьи продукты нужно получить.
     * @return a list of products associated with the client. / список продуктов, связанных с клиентом.
     */
    public List<Product> getAllByClient(Client client) {
        log.debug("Retrieving all products for client: {}", client);
        return productRepository.findAllByClient(client);
    }

    /**
     * Retrieves all products within a specific category or its subcategories.
     * Получает все продукты в определенной категории или ее подкатегориях.
     *
     * @param category the category to filter products. / категория для фильтрации продуктов.
     * @return a list of products in the category or its subcategories. / список продуктов в категории или ее подкатегориях.
     */
    public List<Product> getAllByCategory(Category category) {
        log.debug("Retrieving all products for category: {}", category);
        return productRepository.findAllByCategoryAndHisSubCategory(category);
    }

    public long getHerbaFloraProductsCountByCategory (Category category) {
        return productRepository.countProductsByCategoryAndClientName(category, "Herba Flora");
    }

    /**
     * Updates the extra information associated with a product.
     * Обновляет дополнительную информацию, связанную с продуктом.
     *
     * @param product       the product to update. / продукт, который нужно обновить.
     * @param extraInfoList the list of extra information to associate with the product. / список дополнительной информации для привязки к продукту.
     */
    @Transactional
    public void updateExtraInfo(Product product, List<ProductExtraInfo> extraInfoList) {
        // Загружаем продукт с полной связанной информацией
        Product existingProduct = productRepository.findByIdWithExtraInfo(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + product.getId()));

        // Удаление записей, которых нет в новом списке
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
                // Если запись существует, обновляем её значение и порядок
                existingInfo.setValue(newInfo.getValue());
                existingInfo.setSortOrder(newInfo.getSortOrder());
            }
        });

        // Сохранение обновлений
        productRepository.save(existingProduct);

        // Обновление текущего объекта продукта
        product.setExtraInfo(existingProduct.getExtraInfo());
    }

}
