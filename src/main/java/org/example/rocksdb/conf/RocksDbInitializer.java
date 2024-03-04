package org.example.rocksdb.conf;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RocksDbInitializer {

  private final static String NAME = "rocks-db";

  private RocksDB db;

  @PostConstruct
  void initialize() {
    File dbDir = new File("./dist/db/rocks-db", NAME);
    try {
      RocksDB.loadLibrary();

      final Options options = new Options();
      options.setCreateIfMissing(true);

      Files.createDirectories(dbDir.getParentFile().toPath());
      Files.createDirectories(dbDir.getAbsoluteFile().toPath());
      db = RocksDB.open(options, dbDir.getAbsolutePath());
      log.info("rocks db init name : " + db.getName());
    } catch (IOException | RocksDBException e) {
      log.error(e.getMessage(), e);
    }

  }

  @Bean
  public RocksDB rocksDB() {
    return this.db;
  }

}
