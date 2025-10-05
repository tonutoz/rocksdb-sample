package org.example.rocksdb.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.rocksdb.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    // Clean up before each test
    List<User> allUsers = userRepository.findAll();
    for (User user : allUsers) {
      userRepository.delete(user.getId());
    }
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    List<User> allUsers = userRepository.findAll();
    for (User user : allUsers) {
      userRepository.delete(user.getId());
    }
  }

  @Test
  @DisplayName("사용자 저장 및 조회 테스트")
  void testSaveAndFind() {
    // Given
    User user = User.builder()
        .id("user1")
        .name("John Doe")
        .age(30)
        .build();

    // When
    userRepository.save("user1", user);
    User found = userRepository.find("user1");

    // Then
    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo("user1");
    assertThat(found.getName()).isEqualTo("John Doe");
    assertThat(found.getAge()).isEqualTo(30);
  }

  @Test
  @DisplayName("사용자 삭제 테스트")
  void testDelete() {
    // Given
    User user = User.builder()
        .id("user2")
        .name("Jane Doe")
        .age(25)
        .build();
    userRepository.save("user2", user);

    // When
    userRepository.delete("user2");
    User found = userRepository.find("user2");

    // Then
    assertThat(found).isNull();
  }

  @Test
  @DisplayName("모든 사용자 조회 테스트")
  void testFindAll() {
    // Given
    User user1 = User.builder().id("user1").name("User 1").age(20).build();
    User user2 = User.builder().id("user2").name("User 2").age(30).build();
    User user3 = User.builder().id("user3").name("User 3").age(40).build();

    userRepository.save("user1", user1);
    userRepository.save("user2", user2);
    userRepository.save("user3", user3);

    // When
    List<User> allUsers = userRepository.findAll();

    // Then
    assertThat(allUsers).hasSize(3);
  }

  @Test
  @DisplayName("Prefix로 사용자 검색 테스트")
  void testFindByPrefix() {
    // Given
    User admin1 = User.builder().id("admin1").name("Admin 1").age(35).build();
    User admin2 = User.builder().id("admin2").name("Admin 2").age(40).build();
    User user1 = User.builder().id("user1").name("User 1").age(25).build();

    userRepository.save("admin1", admin1);
    userRepository.save("admin2", admin2);
    userRepository.save("user1", user1);

    // When
    List<User> admins = userRepository.findByPrefix("admin");

    // Then
    assertThat(admins).hasSize(2);
    assertThat(admins).allMatch(user -> user.getId().startsWith("admin"));
  }

  @Test
  @DisplayName("배치 저장 테스트")
  void testSaveAll() {
    // Given
    Map<String, User> users = new HashMap<>();
    users.put("batch1", User.builder().id("batch1").name("Batch 1").age(20).build());
    users.put("batch2", User.builder().id("batch2").name("Batch 2").age(30).build());
    users.put("batch3", User.builder().id("batch3").name("Batch 3").age(40).build());

    // When
    userRepository.saveAll(users);

    // Then
    User user1 = userRepository.find("batch1");
    User user2 = userRepository.find("batch2");
    User user3 = userRepository.find("batch3");

    assertThat(user1).isNotNull();
    assertThat(user2).isNotNull();
    assertThat(user3).isNotNull();
  }

  @Test
  @DisplayName("배치 삭제 테스트")
  void testDeleteAll() {
    // Given
    userRepository.save("delete1", User.builder().id("delete1").name("Delete 1").age(20).build());
    userRepository.save("delete2", User.builder().id("delete2").name("Delete 2").age(30).build());
    userRepository.save("delete3", User.builder().id("delete3").name("Delete 3").age(40).build());

    // When
    userRepository.deleteAll(List.of("delete1", "delete2", "delete3"));

    // Then
    assertThat(userRepository.find("delete1")).isNull();
    assertThat(userRepository.find("delete2")).isNull();
    assertThat(userRepository.find("delete3")).isNull();
  }

  @Test
  @DisplayName("존재 여부 확인 테스트")
  void testExists() {
    // Given
    User user = User.builder().id("exist1").name("Exist Test").age(30).build();
    userRepository.save("exist1", user);

    // When & Then
    assertThat(userRepository.exists("exist1")).isTrue();
    assertThat(userRepository.exists("nonexistent")).isFalse();
  }

  @Test
  @DisplayName("카운트 테스트")
  void testCount() {
    // Given
    userRepository.save("count1", User.builder().id("count1").name("Count 1").age(20).build());
    userRepository.save("count2", User.builder().id("count2").name("Count 2").age(30).build());
    userRepository.save("count3", User.builder().id("count3").name("Count 3").age(40).build());

    // When
    long count = userRepository.count();

    // Then
    assertThat(count).isEqualTo(3);
  }
}
