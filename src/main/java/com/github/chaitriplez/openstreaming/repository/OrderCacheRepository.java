package com.github.chaitriplez.openstreaming.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCacheRepository extends CrudRepository<OrderCache, Long> {

  List<OrderCache> findBySymbolAndActiveIsTrue(String symbol);

  List<OrderCache> findByActiveIsTrue();
}
