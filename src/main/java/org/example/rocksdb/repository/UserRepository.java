package org.example.rocksdb.repository;

import java.util.Map;
import org.example.rocksdb.conf.ColumnFamilyConfig;
import org.example.rocksdb.model.User;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends RocksDbRepository<User> {

  public UserRepository(RocksDB rocksDB,
      Map<ColumnFamilyConfig, ColumnFamilyHandle> columnFamilyHandleMap) {
    super(rocksDB, columnFamilyHandleMap, ColumnFamilyConfig.USER, User.class);
  }
}
