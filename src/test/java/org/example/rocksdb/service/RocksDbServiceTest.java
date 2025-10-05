package org.example.rocksdb.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.rocksdb.model.Product;
import org.example.rocksdb.model.User;
import org.example.rocksdb.repository.ProductRepository;
import org.example.rocksdb.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RocksDbServiceTest {

  @Autowired
  private RocksDbService rocksDbService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @AfterEach
  void tearDown() {
    // Clean up users
    List<User> allUsers = userRepository.findAll();
    for (User user : allUsers) {
      userRepository.delete(user.getId());
    }

    // Clean up products
    List<Product> allProducts = productRepository.findAll();
    for (Product product : allProducts) {
      productRepository.delete(product.getId());
    }
  }

  @Test
  @DisplayName("사용자 저장 및 조회 서비스 테스트")
  void testSaveAndGetUser() {
    // Given
    User user = User.builder()
        .id("user1")
        .name("John Doe")
        .age(30)
        .build();

    // When
    rocksDbService.saveUser("user1", user);
    User found = rocksDbService.getUser("user1");

    // Then
    assertThat(found).isNotNull();
    assertThat(found.getName()).isEqualTo("John Doe");
    assertThat(found.getAge()).isEqualTo(30);
  }

  @Test
  @DisplayName("사용자 삭제 서비스 테스트")
  void testDeleteUser() {
    // Given
    User user = User.builder().id("user2").name("Jane Doe").age(25).build();
    rocksDbService.saveUser("user2", user);

    // When
    rocksDbService.deleteUser("user2");

    // Then
    assertThat(rocksDbService.getUser("user2")).isNull();
  }

  @Test
  @DisplayName("모든 사용자 조회 서비스 테스트")
  void testGetAllUsers() {
    // Given
    rocksDbService.saveUser("u1", User.builder().id("u1").name("User 1").age(20).build());
    rocksDbService.saveUser("u2", User.builder().id("u2").name("User 2").age(30).build());
    rocksDbService.saveUser("u3", User.builder().id("u3").name("User 3").age(40).build());

    // When
    List<User> users = rocksDbService.getAllUsers();

    // Then
    assertThat(users).hasSize(3);
  }

  @Test
  @DisplayName("Prefix로 사용자 검색 서비스 테스트")
  void testGetUsersByPrefix() {
    // Given
    rocksDbService.saveUser("admin1", User.builder().id("admin1").name("Admin 1").age(35).build());
    rocksDbService.saveUser("admin2", User.builder().id("admin2").name("Admin 2").age(40).build());
    rocksDbService.saveUser("user1", User.builder().id("user1").name("User 1").age(25).build());

    // When
    List<User> admins = rocksDbService.getUsersByPrefix("admin");

    // Then
    assertThat(admins).hasSize(2);
  }

  @Test
  @DisplayName("사용자 배치 저장 서비스 테스트")
  void testSaveAllUsers() {
    // Given
    Map<String, User> users = new HashMap<>();
    users.put("batch1", User.builder().id("batch1").name("Batch 1").age(20).build());
    users.put("batch2", User.builder().id("batch2").name("Batch 2").age(30).build());

    // When
    rocksDbService.saveAllUsers(users);

    // Then
    assertThat(rocksDbService.getUser("batch1")).isNotNull();
    assertThat(rocksDbService.getUser("batch2")).isNotNull();
  }

  @Test
  @DisplayName("사용자 존재 여부 확인 서비스 테스트")
  void testUserExists() {
    // Given
    rocksDbService.saveUser("exist1", User.builder().id("exist1").name("Exist").age(30).build());

    // When & Then
    assertThat(rocksDbService.userExists("exist1")).isTrue();
    assertThat(rocksDbService.userExists("nonexistent")).isFalse();
  }

  @Test
  @DisplayName("사용자 카운트 서비스 테스트")
  void testCountUsers() {
    // Given
    rocksDbService.saveUser("c1", User.builder().id("c1").name("Count 1").age(20).build());
    rocksDbService.saveUser("c2", User.builder().id("c2").name("Count 2").age(30).build());

    // When
    long count = rocksDbService.countUsers();

    // Then
    assertThat(count).isEqualTo(2);
  }

  @Test
  @DisplayName("상품 저장 및 조회 서비스 테스트")
  void testSaveAndGetProduct() {
    // Given
    Product product = Product.builder()
        .id("prod1")
        .name("Laptop")
        .price(1200.50)
        .stock(10)
        .build();

    // When
    rocksDbService.saveProduct("prod1", product);
    Product found = rocksDbService.getProduct("prod1");

    // Then
    assertThat(found).isNotNull();
    assertThat(found.getName()).isEqualTo("Laptop");
    assertThat(found.getPrice()).isEqualTo(1200.50);
  }

  @Test
  @DisplayName("상품 삭제 서비스 테스트")
  void testDeleteProduct() {
    // Given
    Product product = Product.builder().id("prod2").name("Mouse").price(25.99).stock(50).build();
    rocksDbService.saveProduct("prod2", product);

    // When
    rocksDbService.deleteProduct("prod2");

    // Then
    assertThat(rocksDbService.getProduct("prod2")).isNull();
  }

  @Test
  @DisplayName("모든 상품 조회 서비스 테스트")
  void testGetAllProducts() {
    // Given
    rocksDbService.saveProduct("p1", Product.builder().id("p1").name("Product 1").price(10.0).stock(5).build());
    rocksDbService.saveProduct("p2", Product.builder().id("p2").name("Product 2").price(20.0).stock(10).build());

    // When
    List<Product> products = rocksDbService.getAllProducts();

    // Then
    assertThat(products).hasSize(2);
  }
}
