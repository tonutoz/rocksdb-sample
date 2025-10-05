package org.example.rocksdb.model;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Product implements Serializable {

  private String id;

  private String name;

  private double price;

  private int stock;

  @Builder
  public Product(String id, String name, double price, int stock) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.stock = stock;
  }
}
