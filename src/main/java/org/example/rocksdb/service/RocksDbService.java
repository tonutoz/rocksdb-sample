package org.example.rocksdb.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.Product;
import org.example.rocksdb.model.User;
import org.example.rocksdb.repository.ProductRepository;
import org.example.rocksdb.repository.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RocksDbService {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  // User operations
  public User getUser(final String key) {
    return userRepository.find(key);
  }

  public User saveUser(final String key, final User user) {
    userRepository.save(key, user);
    return user;
  }

  public void deleteUser(final String key) {
    userRepository.delete(key);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public List<User> getUsersByPrefix(final String prefix) {
    return userRepository.findByPrefix(prefix);
  }

  public void saveAllUsers(final Map<String, User> users) {
    userRepository.saveAll(users);
  }

  public void deleteAllUsers(final List<String> keys) {
    userRepository.deleteAll(keys);
  }

  public boolean userExists(final String key) {
    return userRepository.exists(key);
  }

  public long countUsers() {
    return userRepository.count();
  }

  // Product operations
  public Product getProduct(final String key) {
    return productRepository.find(key);
  }

  public Product saveProduct(final String key, final Product product) {
    productRepository.save(key, product);
    return product;
  }

  public void deleteProduct(final String key) {
    productRepository.delete(key);
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public List<Product> getProductsByPrefix(final String prefix) {
    return productRepository.findByPrefix(prefix);
  }

  public void saveAllProducts(final Map<String, Product> products) {
    productRepository.saveAll(products);
  }

  public void deleteAllProducts(final List<String> keys) {
    productRepository.deleteAll(keys);
  }

  public boolean productExists(final String key) {
    return productRepository.exists(key);
  }

  public long countProducts() {
    return productRepository.count();
  }
}
