package org.example.rocksdb.repository;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.User;
import org.example.rocksdb.util.SerializationUtils;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RocksDbRepository implements KeyValueRepository<String, User> {

  private final RocksDB rocksDB;

  @Override
  public void save(String key, User value) {
    try {
      rocksDB.put(key.getBytes(), SerializationUtils.serialize(value));
    } catch (IOException | RocksDBException e) {
      log.error("Error retrieving the entry in RocksDB from key: {}, cause: {}, message: {}", key,
          e.getCause(), e.getMessage());
    }
  }

  @Override
  public User find(final String key) {
    log.info("find key {}", key);
    User result = null;
    try {
      byte[] bytes = rocksDB.get(key.getBytes());
      if (bytes == null) {
        return null;
      }

      result = SerializationUtils.deserialize(bytes, User.class);

    } catch (RocksDBException | IOException | ClassNotFoundException e) {
      log.error("Error retrieving the entry in RocksDB from key: {}, cause: {}, message: {}", key,
          e.getCause(), e.getMessage());
    }
    return result;
  }

  @Override
  public void delete(String key) {
    log.info("delete key {}", key);
    try {
      rocksDB.delete(key.getBytes());
    } catch (RocksDBException e) {
      log.error("Error deleting entry in RocksDB, cause: {}, message: {}", e.getCause(),
          e.getMessage());
    }
  }
}
