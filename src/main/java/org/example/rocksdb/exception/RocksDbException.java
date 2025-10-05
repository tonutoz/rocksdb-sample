package org.example.rocksdb.exception;

public class RocksDbException extends RuntimeException {

  public RocksDbException(String message) {
    super(message);
  }

  public RocksDbException(String message, Throwable cause) {
    super(message, cause);
  }
}
