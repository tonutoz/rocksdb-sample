package org.example.rocksdb.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserDto{

  private String id;

  private String name;

  private int age;

  @Builder
  public UserDto(String id, String name, int age) {
    this.id = id;
    this.name = name;
    this.age = age;
  }
}
