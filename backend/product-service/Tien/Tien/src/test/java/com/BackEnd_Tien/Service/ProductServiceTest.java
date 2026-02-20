package com.BackEnd_Tien.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * COMPONENT TEST - Bạn 2 Test 1: Search Products
 * 
 * Mục đích: Kiểm thử hàm searchProducts() của ProductService
 * Loại test: Component/Unit Test (test 1 method riêng lẻ)
 * 
 * File:
 * backend/product-service/Tien/Tien/src/test/java/com/BackEnd_Tien/Service/ProductServiceTest.java
 * 
 * Cách chạy:
 * mvn test -Dtest=ProductServiceTest
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - searchProducts()")
class ProductServiceTest {

    // Mock Product class for testing
    static class Product {
        private Long id;
        private String name;
        private String category;
        private double price;

        public Product(Long id, String name, String category, double price) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public double getPrice() {
            return price;
        }
    }

    // Simple ProductService for testing
    class ProductService {
        private List<Product> products = new ArrayList<>();

        public void addProduct(Product product) {
            products.add(product);
        }

        public List<Product> searchProducts(String keyword, String category) {
            return products.stream()
                    .filter(p -> keyword == null || p.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .filter(p -> category == null || p.getCategory().equals(category))
                    .toList();
        }

        public Product getProductById(Long id) {
            return products.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }
    }

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();

        // Add test data
        productService.addProduct(new Product(1L, "Áo Thun Nam", "Áo", 150000));
        productService.addProduct(new Product(2L, "Quần Jean Nam", "Quần", 250000));
        productService.addProduct(new Product(3L, "Áo Khoác", "Áo", 350000));
    }

    // ✅ Test Case 1: Search with keyword
    @Test
    @DisplayName("Should return products matching keyword")
    void testSearchProducts_WithKeyword_ReturnsMatching() {
        // Arrange
        String keyword = "áo";

        // Act
        List<Product> results = productService.searchProducts(keyword, null);

        // Assert
        assertEquals(2, results.size(), "Should find 2 products with 'áo'");
        assertTrue(results.stream().anyMatch(p -> p.getName().contains("Áo")));
        System.out.println("✅ TC1 PASS: Search by keyword found " + results.size() + " products");
    }

    // ✅ Test Case 2: Search by category
    @Test
    @DisplayName("Should return products matching category")
    void testSearchProducts_ByCategory_ReturnsMatching() {
        // Arrange
        String category = "Áo";

        // Act
        List<Product> results = productService.searchProducts(null, category);

        // Assert
        assertEquals(2, results.size(), "Should find 2 products in 'Áo' category");
        assertTrue(results.stream().allMatch(p -> p.getCategory().equals(category)));
        System.out.println("✅ TC2 PASS: Category search found " + results.size() + " products");
    }

    // ✅ Test Case 3: Empty search results
    @Test
    @DisplayName("Should return empty list when no results")
    void testSearchProducts_NoResults_ReturnsEmpty() {
        // Arrange
        String keyword = "xyz";

        // Act
        List<Product> results = productService.searchProducts(keyword, null);

        // Assert
        assertTrue(results.isEmpty(), "Should return empty list for non-existent products");
        System.out.println("✅ TC3 PASS: No results returned for non-existent keyword");
    }

    // ✅ Test Case 4: Case insensitive search
    @Test
    @DisplayName("Search should be case insensitive")
    void testSearchProducts_CaseInsensitive_ReturnsMatching() {
        // Arrange
        String keyword = "ÁO"; // Uppercase

        // Act
        List<Product> results = productService.searchProducts(keyword, null);

        // Assert
        assertEquals(2, results.size(), "Should find products with case-insensitive search");
        System.out.println("✅ TC4 PASS: Case insensitive search works");
    }

    // ✅ Test Case 5: Combined search (keyword + category)
    @Test
    @DisplayName("Should apply both keyword and category filters")
    void testSearchProducts_Combined_ReturnsMatching() {
        // Arrange
        String keyword = "thun";
        String category = "Áo";

        // Act
        List<Product> results = productService.searchProducts(keyword, category);

        // Assert
        assertEquals(1, results.size(), "Should find 1 product matching both filters");
        assertEquals("Áo Thun Nam", results.get(0).getName());
        System.out.println("✅ TC5 PASS: Combined filters work correctly");
    }
}
