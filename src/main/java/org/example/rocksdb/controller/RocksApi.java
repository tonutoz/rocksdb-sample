package org.example.rocksdb.controller;


import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.Product;
import org.example.rocksdb.model.User;
import org.example.rocksdb.service.RocksDbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/rocksdb")
@RequiredArgsConstructor
public class RocksApi {

  private final RocksDbService dbService;

  // User endpoints
  @PostMapping("/users/{key}")
  public ResponseEntity<User> saveUser(@PathVariable("key") String key,
      @RequestBody User request) {
    log.info("Saving user with key: {}", key);
    User saved = dbService.saveUser(key, request);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/users/{key}")
  public ResponseEntity<User> getUser(@PathVariable("key") String key) {
    log.info("Getting user with key: {}", key);
    User user = dbService.getUser(key);
    return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
  }

  @DeleteMapping("/users/{key}")
  public ResponseEntity<Void> deleteUser(@PathVariable("key") String key) {
    log.info("Deleting user with key: {}", key);
    dbService.deleteUser(key);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    log.info("Getting all users");
    List<User> users = dbService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/users/search")
  public ResponseEntity<List<User>> searchUsersByPrefix(@RequestParam String prefix) {
    log.info("Searching users by prefix: {}", prefix);
    List<User> users = dbService.getUsersByPrefix(prefix);
    return ResponseEntity.ok(users);
  }

  @PostMapping("/users/batch")
  public ResponseEntity<Void> saveAllUsers(@RequestBody Map<String, User> users) {
    log.info("Batch saving {} users", users.size());
    dbService.saveAllUsers(users);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users/batch")
  public ResponseEntity<Void> deleteAllUsers(@RequestBody List<String> keys) {
    log.info("Batch deleting {} users", keys.size());
    dbService.deleteAllUsers(keys);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/users/{key}/exists")
  public ResponseEntity<Boolean> userExists(@PathVariable("key") String key) {
    log.info("Checking if user exists with key: {}", key);
    boolean exists = dbService.userExists(key);
    return ResponseEntity.ok(exists);
  }

  @GetMapping("/users/count")
  public ResponseEntity<Long> countUsers() {
    log.info("Counting users");
    long count = dbService.countUsers();
    return ResponseEntity.ok(count);
  }

  // Product endpoints
  @PostMapping("/products/{key}")
  public ResponseEntity<Product> saveProduct(@PathVariable("key") String key,
      @RequestBody Product request) {
    log.info("Saving product with key: {}", key);
    Product saved = dbService.saveProduct(key, request);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/products/{key}")
  public ResponseEntity<Product> getProduct(@PathVariable("key") String key) {
    log.info("Getting product with key: {}", key);
    Product product = dbService.getProduct(key);
    return product == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(product);
  }

  @DeleteMapping("/products/{key}")
  public ResponseEntity<Void> deleteProduct(@PathVariable("key") String key) {
    log.info("Deleting product with key: {}", key);
    dbService.deleteProduct(key);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/products")
  public ResponseEntity<List<Product>> getAllProducts() {
    log.info("Getting all products");
    List<Product> products = dbService.getAllProducts();
    return ResponseEntity.ok(products);
  }

  @GetMapping("/products/search")
  public ResponseEntity<List<Product>> searchProductsByPrefix(@RequestParam String prefix) {
    log.info("Searching products by prefix: {}", prefix);
    List<Product> products = dbService.getProductsByPrefix(prefix);
    return ResponseEntity.ok(products);
  }

  @PostMapping("/products/batch")
  public ResponseEntity<Void> saveAllProducts(@RequestBody Map<String, Product> products) {
    log.info("Batch saving {} products", products.size());
    dbService.saveAllProducts(products);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/products/batch")
  public ResponseEntity<Void> deleteAllProducts(@RequestBody List<String> keys) {
    log.info("Batch deleting {} products", keys.size());
    dbService.deleteAllProducts(keys);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/products/{key}/exists")
  public ResponseEntity<Boolean> productExists(@PathVariable("key") String key) {
    log.info("Checking if product exists with key: {}", key);
    boolean exists = dbService.productExists(key);
    return ResponseEntity.ok(exists);
  }

  @GetMapping("/products/count")
  public ResponseEntity<Long> countProducts() {
    log.info("Counting products");
    long count = dbService.countProducts();
    return ResponseEntity.ok(count);
  }
}
