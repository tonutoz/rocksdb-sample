# RocksDB Spring Boot Sample

RocksDB를 Spring Boot와 통합한 학습용 샘플 프로젝트입니다. Column Family, Batch 작업, Range Query 등 RocksDB의 핵심 기능을 **웹 UI**를 통해 학습할 수 있습니다.

## 📋 프로젝트 개요

- **Spring Boot**: 3.2.3
- **Java**: 21
- **RocksDB**: 8.8.1
- **Template Engine**: Thymeleaf
- **Build Tool**: Gradle 8.5

## 🖥️ 웹 UI 제공

이제 **브라우저**에서 직접 테스트할 수 있습니다!

```bash
# 애플리케이션 실행
./gradlew bootRun

# 브라우저에서 접속
http://localhost:8099
```

### 제공되는 화면
1. **메인 대시보드** (`/`)
   - 사용자/상품 개수 확인
   - 각 관리 페이지로 이동

2. **사용자 관리** (`/users`)
   - 사용자 등록/조회/삭제
   - Prefix 검색 기능

3. **상품 관리** (`/products`)
   - 상품 등록/조회/삭제
   - Prefix 검색 기능

## 🎯 학습 목표

### ✅ 구현 완료
1. **Column Family 구조**
   - User, Product 도메인별 Column Family 분리
   - 성능 최적화 설정 (LZ4 압축, LRU Cache, Bloom Filter)

2. **CRUD 작업**
   - 기본 저장/조회/삭제
   - Batch 작업 (saveAll, deleteAll)
   - Range Query (findAll, findByPrefix)

3. **직렬화**
   - JSON 직렬화 (Jackson) 사용
   - 기존 Java Serialization 대비 성능 및 호환성 개선

4. **예외 처리**
   - Custom Exception (RocksDbException, DataNotFoundException)
   - 명확한 에러 메시지

5. **테스트**
   - Repository 테스트 (UserRepositoryTest, ProductRepositoryTest)
   - Service 테스트 (RocksDbServiceTest)
   - 총 25개 테스트 케이스 작성

## 🏗️ 아키텍처

```
Controller Layer (RocksApi)
    ↓
Service Layer (RocksDbService)
    ↓
Repository Layer (UserRepository, ProductRepository)
    ↓
RocksDB (Column Families: users, products, default)
```

## 📦 주요 구성 요소

### 1. Configuration
- **RocksDbInitializer**: RocksDB 초기화 및 Column Family 설정
- **ColumnFamilyConfig**: Column Family enum 정의

### 2. Model
- **User**: 사용자 도메인 (id, name, age)
- **Product**: 상품 도메인 (id, name, price, stock)

### 3. Repository
- **RocksDbRepository**: 추상 베이스 클래스
  - Column Family 지원
  - Iterator를 활용한 Range Query
  - WriteBatch를 활용한 Batch 작업
- **UserRepository**: User 전용 Repository
- **ProductRepository**: Product 전용 Repository

### 4. Service
- **RocksDbService**: 비즈니스 로직 처리

### 5. Controller
- **RocksApi**: REST API 엔드포인트

## 🚀 엔드포인트

### 웹 UI (Thymeleaf)
```
GET  /              - 메인 대시보드
GET  /users         - 사용자 목록 및 검색
POST /users/save    - 사용자 저장
GET  /users/delete/{id} - 사용자 삭제
GET  /products      - 상품 목록 및 검색
POST /products/save - 상품 저장
GET  /products/delete/{id} - 상품 삭제
```

### REST API
```http
# 사용자 API
POST   /api/rocksdb/users/{key}         - 사용자 저장
GET    /api/rocksdb/users/{key}         - 사용자 조회
DELETE /api/rocksdb/users/{key}         - 사용자 삭제
GET    /api/rocksdb/users               - 모든 사용자 조회
GET    /api/rocksdb/users/search?prefix - Prefix 검색
POST   /api/rocksdb/users/batch         - 배치 저장
DELETE /api/rocksdb/users/batch         - 배치 삭제
GET    /api/rocksdb/users/{key}/exists  - 존재 여부
GET    /api/rocksdb/users/count         - 카운트

# 상품 API (동일한 패턴)
/api/rocksdb/products/...
```

## 🔧 RocksDB 성능 튜닝 설정

### Write Buffer
```java
cfOptions.setWriteBufferSize(64 * 1024 * 1024); // 64MB
```

### Block Cache (읽기 성능)
```java
tableConfig.setBlockCache(new LRUCache(256 * 1024 * 1024)); // 256MB
```

### Bloom Filter (존재 여부 빠른 확인)
```java
tableConfig.setFilterPolicy(new BloomFilter(10));
```

### Compression
```java
cfOptions.setCompressionType(CompressionType.LZ4_COMPRESSION);
```

## 🧪 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests UserRepositoryTest
./gradlew test --tests RocksDbServiceTest
```

## 💡 주요 학습 포인트

### 1. Column Family 사용법
```java
// Repository 생성 시 Column Family 지정
public UserRepository(RocksDB rocksDB,
    Map<ColumnFamilyConfig, ColumnFamilyHandle> columnFamilyHandleMap) {
  super(rocksDB, columnFamilyHandleMap, ColumnFamilyConfig.USER, User.class);
}
```

### 2. Range Query (Prefix Scan)
```java
public List<V> findByPrefix(String prefix) {
  List<V> results = new ArrayList<>();
  byte[] prefixBytes = prefix.getBytes();
  try (RocksIterator iterator = rocksDB.newIterator(columnFamilyHandle)) {
    iterator.seek(prefixBytes);
    while (iterator.isValid()) {
      byte[] key = iterator.key();
      if (!startsWith(key, prefixBytes)) break;
      results.add(deserialize(iterator.value()));
      iterator.next();
    }
  }
  return results;
}
```

### 3. Batch 작업
```java
public void saveAll(Map<String, V> entries) {
  try (WriteBatch batch = new WriteBatch();
       WriteOptions writeOptions = new WriteOptions()) {
    for (Map.Entry<String, V> entry : entries.entrySet()) {
      batch.put(columnFamilyHandle,
                entry.getKey().getBytes(),
                serialize(entry.getValue()));
    }
    rocksDB.write(writeOptions, batch);
  }
}
```

### 4. Iterator 사용
```java
public List<V> findAll() {
  List<V> results = new ArrayList<>();
  try (RocksIterator iterator = rocksDB.newIterator(columnFamilyHandle)) {
    iterator.seekToFirst();
    while (iterator.isValid()) {
      results.add(deserialize(iterator.value()));
      iterator.next();
    }
  }
  return results;
}
```

## 📚 추가 학습 과제

### 1. TTL (Time To Live)
- 일정 시간 후 자동 삭제되는 데이터
- `TtlDB.open()` 사용

### 2. Transaction
- ACID 보장이 필요한 작업
- `TransactionDB` 사용

### 3. Backup/Restore
- 데이터베이스 백업 및 복구
- `BackupEngine` 사용

### 4. Merge Operator
- 값을 읽지 않고 업데이트
- 카운터 증가 등에 유용

### 5. Monitoring
- Spring Actuator와 통합
- RocksDB Statistics 노출

## 🐛 알려진 이슈

1. Windows 환경에서 RocksDB 파일 락 문제
   - 해결: 테스트 실행 전 `dist/db` 디렉토리 삭제

2. Controller 통합 테스트 실패
   - 원인: 동시에 같은 DB를 열려는 시도
   - 해결: Service 레이어 테스트로 대체

## 📖 참고 자료

- [RocksDB Wiki](https://github.com/facebook/rocksdb/wiki)
- [RocksDB Java Documentation](https://github.com/facebook/rocksdb/wiki/RocksJava-Basics)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## 🔍 프로젝트 구조

```
src/
├── main/
│   ├── java/org/example/rocksdb/
│   │   ├── conf/           # RocksDB 설정
│   │   ├── controller/     # REST API 컨트롤러
│   │   ├── exception/      # 커스텀 예외
│   │   ├── model/          # 도메인 모델
│   │   ├── repository/     # 데이터 액세스 레이어
│   │   ├── service/        # 비즈니스 로직
│   │   └── util/           # 유틸리티 클래스
│   └── resources/
│       └── application.yml # 애플리케이션 설정
└── test/
    ├── java/org/example/rocksdb/
    │   ├── repository/     # Repository 테스트
    │   └── service/        # Service 테스트
    └── resources/
        └── application-test.yml
```

## 🎓 학습 순서 추천

1. **Week 1**: Column Family 이해 및 CRUD 구현
2. **Week 2**: Iterator를 활용한 Range Query
3. **Week 3**: Batch 작업 및 성능 최적화
4. **Week 4**: 예외 처리 및 테스트 작성
5. **Week 5**: 고급 기능 (TTL, Transaction 등)

---

**Happy Learning! 🚀**
