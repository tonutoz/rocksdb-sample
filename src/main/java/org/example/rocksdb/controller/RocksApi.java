package org.example.rocksdb.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rocksdb.model.User;
import org.example.rocksdb.service.RocksDbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/rocksdb/")
@RequiredArgsConstructor
public class RocksApi {

  private final RocksDbService dbService;

  @PostMapping("/{key}")
  public ResponseEntity<?> save(@PathVariable("key") String key, @RequestBody User request) {
    log.info("RocksApi.save");
    return ResponseEntity.ok(dbService.addData(key,request));
  }

  @GetMapping("/{key}")
  public ResponseEntity<?> find(@PathVariable("key") String key) {
    log.info("RocksApi.find");
    User result = dbService.getData(key);
    return result ==null ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
  }

  @DeleteMapping("/{key}")
  public ResponseEntity<?> delete(@PathVariable("key") String key) {
    log.info("RocksApi.delete");
    return ResponseEntity.ok(key);
  }

}
