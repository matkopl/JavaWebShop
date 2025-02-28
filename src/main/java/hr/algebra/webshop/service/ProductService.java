package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.model.Product;
import hr.algebra.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product ->
                    new ProductDto(
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getStock()))
                .toList();
    }

    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> new ProductDto(
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock()
                ));
    }

    public ProductDto addProduct(ProductDto productDto) {
        Product product = new Product(
                null,
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                productDto.getStock()
        );

        productRepository.save(product);

        return new ProductDto(
                product.getName(),
                product.getDescription(),
                productDto.getPrice(),
                productDto.getStock()
        );
    }

    public Optional<ProductDto> updateProduct(Long id, ProductDto productDto) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDto.getName());
                    existingProduct.setDescription(productDto.getDescription());
                    existingProduct.setPrice(productDto.getPrice());
                    existingProduct.setStock(productDto.getStock());
                    productRepository.save(existingProduct);
                    return new ProductDto(
                            existingProduct.getName(),
                            existingProduct.getDescription(),
                            existingProduct.getPrice(),
                            existingProduct.getStock()
                    );
                });
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
