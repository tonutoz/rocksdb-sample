package org.example.rocksdb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.rocksdb.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    List<Product> allProducts = productRepository.findAll();
    for (Product product : allProducts) {
      productRepository.delete(product.getId());
    }
  }

  @AfterEach
  void tearDown() {
    List<Product> allProducts = productRepository.findAll();
    for (Product product : allProducts) {
      productRepository.delete(product.getId());
    }
  }

  @Test
  @DisplayName("상품 저장 및 조회 테스트")
  void testSaveAndFind() {
    // Given
    Product product = Product.builder()
        .id("prod1")
        .name("Laptop")
        .price(1200.50)
        .stock(10)
        .build();

    // When
    productRepository.save("prod1", product);
    Product found = productRepository.find("prod1");

    // Then
    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo("prod1");
    assertThat(found.getName()).isEqualTo("Laptop");
    assertThat(found.getPrice()).isEqualTo(1200.50);
    assertThat(found.getStock()).isEqualTo(10);
  }

  @Test
  @DisplayName("상품 삭제 테스트")
  void testDelete() {
    // Given
    Product product = Product.builder()
        .id("prod2")
        .name("Mouse")
        .price(25.99)
        .stock(50)
        .build();
    productRepository.save("prod2", product);

    // When
    productRepository.delete("prod2");
    Product found = productRepository.find("prod2");

    // Then
    assertThat(found).isNull();
  }

  @Test
  @DisplayName("모든 상품 조회 테스트")
  void testFindAll() {
    // Given
    productRepository.save("p1", Product.builder().id("p1").name("Product 1").price(10.0).stock(5).build());
    productRepository.save("p2", Product.builder().id("p2").name("Product 2").price(20.0).stock(10).build());
    productRepository.save("p3", Product.builder().id("p3").name("Product 3").price(30.0).stock(15).build());

    // When
    List<Product> allProducts = productRepository.findAll();

    // Then
    assertThat(allProducts).hasSize(3);
  }

  @Test
  @DisplayName("배치 저장 테스트")
  void testSaveAll() {
    // Given
    Map<String, Product> products = new HashMap<>();
    products.put("batch1", Product.builder().id("batch1").name("Batch 1").price(100.0).stock(5).build());
    products.put("batch2", Product.builder().id("batch2").name("Batch 2").price(200.0).stock(10).build());

    // When
    productRepository.saveAll(products);

    // Then
    assertThat(productRepository.find("batch1")).isNotNull();
    assertThat(productRepository.find("batch2")).isNotNull();
  }

  @Test
  @DisplayName("상품 존재 여부 확인 테스트")
  void testExists() {
    // Given
    productRepository.save("exist1", Product.builder().id("exist1").name("Exist Test").price(50.0).stock(3).build());

    // When & Then
    assertThat(productRepository.exists("exist1")).isTrue();
    assertThat(productRepository.exists("nonexistent")).isFalse();
  }

  @Test
  @DisplayName("상품 카운트 테스트")
  void testCount() {
    // Given
    productRepository.save("c1", Product.builder().id("c1").name("Count 1").price(10.0).stock(1).build());
    productRepository.save("c2", Product.builder().id("c2").name("Count 2").price(20.0).stock(2).build());

    // When
    long count = productRepository.count();

    // Then
    assertThat(count).isEqualTo(2);
  }
}
