package org.example.rocksdb.conf;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.BloomFilter;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.DBOptions;
import org.rocksdb.LRUCache;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RocksDbInitializer {

  private final static String NAME = "rocks-db";

  private RocksDB db;
  private Map<ColumnFamilyConfig, ColumnFamilyHandle> columnFamilyHandleMap;
  private List<ColumnFamilyHandle> columnFamilyHandles;

  @PostConstruct
  void initialize() {
    File dbDir = new File("./dist/db/rocks-db", NAME);
    DBOptions dbOptions = null;
    ColumnFamilyOptions cfOptions = null;

    try {
      RocksDB.loadLibrary();

      // DBOptions 설정
      dbOptions = new DBOptions();
      dbOptions.setCreateIfMissing(true);
      dbOptions.setCreateMissingColumnFamilies(true);

      // ColumnFamilyOptions 설정 (성능 튜닝)
      cfOptions = new ColumnFamilyOptions();
      cfOptions.setCompressionType(CompressionType.LZ4_COMPRESSION);
      cfOptions.setWriteBufferSize(64 * 1024 * 1024); // 64MB

      // Block Cache 설정 (읽기 성능 향상)
      BlockBasedTableConfig tableConfig = new BlockBasedTableConfig();
      tableConfig.setBlockCache(new LRUCache(256 * 1024 * 1024)); // 256MB
      tableConfig.setFilterPolicy(new BloomFilter(10)); // Bloom Filter
      cfOptions.setTableFormatConfig(tableConfig);

      // Column Family 설정
      List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>();
      columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
          RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions(cfOptions)));
      columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
          ColumnFamilyConfig.USER.getBytes(), new ColumnFamilyOptions(cfOptions)));
      columnFamilyDescriptors.add(new ColumnFamilyDescriptor(
          ColumnFamilyConfig.PRODUCT.getBytes(), new ColumnFamilyOptions(cfOptions)));

      // 디렉토리 생성
      Files.createDirectories(dbDir.getParentFile().toPath());
      Files.createDirectories(dbDir.getAbsoluteFile().toPath());

      // RocksDB 열기
      columnFamilyHandles = new ArrayList<>();
      db = RocksDB.open(dbOptions, dbDir.getAbsolutePath(),
          columnFamilyDescriptors, columnFamilyHandles);

      // Column Family Handle Map 생성
      columnFamilyHandleMap = new HashMap<>();
      columnFamilyHandleMap.put(ColumnFamilyConfig.DEFAULT, columnFamilyHandles.get(0));
      columnFamilyHandleMap.put(ColumnFamilyConfig.USER, columnFamilyHandles.get(1));
      columnFamilyHandleMap.put(ColumnFamilyConfig.PRODUCT, columnFamilyHandles.get(2));

      log.info("RocksDB initialized with column families: {}",
          Arrays.toString(ColumnFamilyConfig.values()));
    } catch (IOException | RocksDBException e) {
      log.error("Failed to initialize RocksDB", e);
      throw new RuntimeException("RocksDB initialization failed", e);
    } finally {
      if (dbOptions != null) {
        dbOptions.close();
      }
      if (cfOptions != null) {
        cfOptions.close();
      }
    }
  }

  @Bean
  public RocksDB rocksDB() {
    return this.db;
  }

  @Bean
  public Map<ColumnFamilyConfig, ColumnFamilyHandle> columnFamilyHandleMap() {
    return this.columnFamilyHandleMap;
  }

  @PreDestroy
  void destroy() {
    if (columnFamilyHandles != null) {
      columnFamilyHandles.forEach(ColumnFamilyHandle::close);
    }
    if (db != null) {
      db.close();
    }
    log.info("RocksDB closed");
  }

}
