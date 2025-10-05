package org.example.rocksdb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.rocksdb.exception.RocksDbException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonSerializationUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static <T> byte[] serialize(T object) {
    try {
      return objectMapper.writeValueAsBytes(object);
    } catch (JsonProcessingException e) {
      throw new RocksDbException("Failed to serialize object to JSON", e);
    }
  }

  public static <T> T deserialize(byte[] data, Class<T> objectType) {
    try {
      return objectMapper.readValue(data, objectType);
    } catch (IOException e) {
      throw new RocksDbException("Failed to deserialize JSON to object", e);
    }
  }
}
