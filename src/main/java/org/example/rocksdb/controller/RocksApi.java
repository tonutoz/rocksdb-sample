package org.example.rocksdb.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @PostMapping("/{key}")
  public ResponseEntity<String> save(@PathVariable("key") String key, @RequestBody String value) {
    log.info("RocksApi.save");
    rocksDB.save(key, value);
    return ResponseEntity.ok(value);
  }

  @GetMapping("/{key}")
  public ResponseEntity<String> find(@PathVariable("key") String key) {
    log.info("RocksApi.find");
    String result = rocksDB.find(key);
    if(result == null) return ResponseEntity.noContent().build();
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{key}")
  public ResponseEntity<String> delete(@PathVariable("key") String key) {
    log.info("RocksApi.delete");
    rocksDB.delete(key);
    return ResponseEntity.ok(key);
  }

}
