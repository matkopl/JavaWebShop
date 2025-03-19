package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.exceptions.EntityNotFoundException;
import hr.algebra.webshop.model.Category;
import hr.algebra.webshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import hr.algebra.webshop.dto.CategoryDto;

import java.util.List;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream().map(this::toDto)
                .toList();
    }


    public Optional<CategoryDto> getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));

        return Optional.of(toDto(category));
    }

    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());

        return toDto(categoryRepository.save(category));
    }

    public Optional<CategoryDto> updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));

        category.setName(categoryDto.getName());
        return Optional.of(toDto(categoryRepository.save(category)));
    }

    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CategoryDto toDto(Category category) {
        List<ProductDto> products = category.getProducts()
                .stream()
                .map(product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getQuantity(),
                        category.getId())).toList();

        return new CategoryDto(category.getId(), category.getName(), products);
    }

}
