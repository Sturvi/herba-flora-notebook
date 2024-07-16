package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.ProductRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.FileService;
import com.example.inovasiyanotebook.service.viewservices.product.technicalreview.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
