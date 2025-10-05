package org.example.rocksdb.exception;

public class DataNotFoundException extends RocksDbException {

  public DataNotFoundException(String key) {
    super("Data not found for key: " + key);
  }
}
