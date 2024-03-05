package org.example.rocksdb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.User;
import org.example.rocksdb.repository.KeyValueRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RocksDbService {

  private final KeyValueRepository<String, User> keyValueRepository;

  public User getData(final String key) {
    return keyValueRepository.find(key);
  }

  public User addData(final String key, final User request) {
    keyValueRepository.save(key,request);
    return request;
  }

  public void deleteData(final String key) {
    keyValueRepository.delete(key);
  }


}
