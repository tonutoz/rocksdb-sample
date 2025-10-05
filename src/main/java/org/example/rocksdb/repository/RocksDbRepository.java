package org.example.rocksdb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.conf.ColumnFamilyConfig;
import org.example.rocksdb.exception.RocksDbException;
import org.example.rocksdb.util.JsonSerializationUtils;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

@Slf4j
public abstract class RocksDbRepository<V> implements KeyValueRepository<String, V> {

  protected final RocksDB rocksDB;
  protected final ColumnFamilyHandle columnFamilyHandle;
  protected final Class<V> valueType;

  protected RocksDbRepository(RocksDB rocksDB,
      Map<ColumnFamilyConfig, ColumnFamilyHandle> columnFamilyHandleMap,
      ColumnFamilyConfig columnFamily, Class<V> valueType) {
    this.rocksDB = rocksDB;
    this.columnFamilyHandle = columnFamilyHandleMap.get(columnFamily);
    this.valueType = valueType;
  }

  @Override
  public void save(String key, V value) {
    try {
      byte[] serializedValue = JsonSerializationUtils.serialize(value);
      rocksDB.put(columnFamilyHandle, key.getBytes(), serializedValue);
      log.debug("Saved key: {}", key);
    } catch (RocksDBException e) {
      log.error("Error saving entry in RocksDB for key: {}", key, e);
      throw new RocksDbException("Failed to save key: " + key, e);
    }
  }

  @Override
  public V find(String key) {
    try {
      byte[] bytes = rocksDB.get(columnFamilyHandle, key.getBytes());
      if (bytes == null) {
        log.debug("Key not found: {}", key);
        return null;
      }
      return JsonSerializationUtils.deserialize(bytes, valueType);
    } catch (RocksDBException e) {
      log.error("Error retrieving entry in RocksDB for key: {}", key, e);
      throw new RocksDbException("Failed to find key: " + key, e);
    }
  }

  @Override
  public void delete(String key) {
    try {
      rocksDB.delete(columnFamilyHandle, key.getBytes());
      log.debug("Deleted key: {}", key);
    } catch (RocksDBException e) {
      log.error("Error deleting entry in RocksDB for key: {}", key, e);
      throw new RocksDbException("Failed to delete key: " + key, e);
    }
  }

  @Override
  public List<V> findAll() {
    List<V> results = new ArrayList<>();
    try (RocksIterator iterator = rocksDB.newIterator(columnFamilyHandle)) {
      iterator.seekToFirst();
      while (iterator.isValid()) {
        byte[] value = iterator.value();
        results.add(JsonSerializationUtils.deserialize(value, valueType));
        iterator.next();
      }
      log.debug("Found {} entries", results.size());
    }
    return results;
  }

  @Override
  public List<V> findByPrefix(String prefix) {
    List<V> results = new ArrayList<>();
    byte[] prefixBytes = prefix.getBytes();
    try (RocksIterator iterator = rocksDB.newIterator(columnFamilyHandle)) {
      iterator.seek(prefixBytes);
      while (iterator.isValid()) {
        byte[] key = iterator.key();
        if (!startsWith(key, prefixBytes)) {
          break;
        }
        byte[] value = iterator.value();
        results.add(JsonSerializationUtils.deserialize(value, valueType));
        iterator.next();
      }
      log.debug("Found {} entries with prefix: {}", results.size(), prefix);
    }
    return results;
  }

  @Override
  public void saveAll(Map<String, V> entries) {
    try (WriteBatch batch = new WriteBatch();
        WriteOptions writeOptions = new WriteOptions()) {
      for (Map.Entry<String, V> entry : entries.entrySet()) {
        byte[] key = entry.getKey().getBytes();
        byte[] value = JsonSerializationUtils.serialize(entry.getValue());
        batch.put(columnFamilyHandle, key, value);
      }
      rocksDB.write(writeOptions, batch);
      log.debug("Saved {} entries in batch", entries.size());
    } catch (RocksDBException e) {
      log.error("Error saving batch entries in RocksDB", e);
      throw new RocksDbException("Failed to save batch entries", e);
    }
  }

  @Override
  public void deleteAll(List<String> keys) {
    try (WriteBatch batch = new WriteBatch();
        WriteOptions writeOptions = new WriteOptions()) {
      for (String key : keys) {
        batch.delete(columnFamilyHandle, key.getBytes());
      }
      rocksDB.write(writeOptions, batch);
      log.debug("Deleted {} entries in batch", keys.size());
    } catch (RocksDBException e) {
      log.error("Error deleting batch entries in RocksDB", e);
      throw new RocksDbException("Failed to delete batch entries", e);
    }
  }

  @Override
  public boolean exists(String key) {
    try {
      byte[] bytes = rocksDB.get(columnFamilyHandle, key.getBytes());
      return bytes != null;
    } catch (RocksDBException e) {
      log.error("Error checking existence in RocksDB for key: {}", key, e);
      throw new RocksDbException("Failed to check existence for key: " + key, e);
    }
  }

  @Override
  public long count() {
    long count = 0;
    try (RocksIterator iterator = rocksDB.newIterator(columnFamilyHandle)) {
      iterator.seekToFirst();
      while (iterator.isValid()) {
        count++;
        iterator.next();
      }
      log.debug("Total count: {}", count);
    }
    return count;
  }

  private boolean startsWith(byte[] array, byte[] prefix) {
    if (array.length < prefix.length) {
      return false;
    }
    for (int i = 0; i < prefix.length; i++) {
      if (array[i] != prefix[i]) {
        return false;
      }
    }
    return true;
  }
}
