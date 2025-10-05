package org.example.rocksdb.conf;

import lombok.Getter;

@Getter
public enum ColumnFamilyConfig {
  USER("users"),
  PRODUCT("products"),
  DEFAULT("default");

  private final String name;

  ColumnFamilyConfig(String name) {
    this.name = name;
  }

  public byte[] getBytes() {
    return name.getBytes();
  }
}
