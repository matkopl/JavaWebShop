package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.exceptions.EntityNotFoundException;
import hr.algebra.webshop.model.Category;
import hr.algebra.webshop.model.Product;
import hr.algebra.webshop.repository.ProductRepository;
import hr.algebra.webshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toDto);
    }

    public ProductDto addProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = new Product(
                null,
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                productDto.getQuantity(),
                category
        );

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    public Optional<ProductDto> updateProduct(Long id, ProductDto productDto) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDto.getName());
                    existingProduct.setDescription(productDto.getDescription());
                    existingProduct.setPrice(productDto.getPrice());
                    existingProduct.setQuantity(productDto.getQuantity());

                    if (productDto.getCategoryId() != null) {
                        Category category = categoryRepository.findById(productDto.getCategoryId())
                                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
                        existingProduct.setCategory(category);
                    }

                    return toDto(productRepository.save(existingProduct));
                });
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory() != null ? product.getCategory().getId() : null
        );
    }
    
    
}
