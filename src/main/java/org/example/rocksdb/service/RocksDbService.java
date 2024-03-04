package org.example.rocksdb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.UserDto;
import org.example.rocksdb.repository.KeyValueRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RocksDbService {


  private final KeyValueRepository<String, UserDto> keyValueRepository;


}
