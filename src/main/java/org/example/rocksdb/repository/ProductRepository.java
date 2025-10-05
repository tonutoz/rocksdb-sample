package org.example.rocksdb.repository;

import java.util.Map;
import org.example.rocksdb.conf.ColumnFamilyConfig;
import org.example.rocksdb.model.Product;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository extends RocksDbRepository<Product> {

  public ProductRepository(RocksDB rocksDB,
      Map<ColumnFamilyConfig, ColumnFamilyHandle> columnFamilyHandleMap) {
    super(rocksDB, columnFamilyHandleMap, ColumnFamilyConfig.PRODUCT, Product.class);
  }
}
