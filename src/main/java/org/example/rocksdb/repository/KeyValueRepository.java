package org.example.rocksdb.repository;

import java.util.List;
import java.util.Map;

public interface KeyValueRepository<K, V> {

  void save(K key, V value);

  V find(K key);

  void delete(K key);

  List<V> findAll();

  List<V> findByPrefix(K prefix);

  void saveAll(Map<K, V> entries);

  void deleteAll(List<K> keys);

  boolean exists(K key);

  long count();
}
