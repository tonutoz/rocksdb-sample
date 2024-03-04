package org.example.rocksdb.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.UserDto;
import org.example.rocksdb.util.SerializationUtils;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RocksDbRepository implements KeyValueRepository<String, UserDto>{

  private final RocksDB rocksDB;

  @Override
  public void save(String key, Object value) {

  }

  @Override
  public UserDto find(final String key) {
    log.info("find key {}", key);
    UserDto result = null;
    try {
      byte[] bytes = rocksDB.get(key.getBytes());
      if(bytes == null) return null;

      result = SerializationUtils.deserialize(bytes, UserDto.class);

    } catch (RocksDBException | ClassNotFoundException e ) {
      log.error("Error retrieving the entry in RocksDB from key: {}, cause: {}, message: {}", key, e.getCause(), e.getMessage());
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
