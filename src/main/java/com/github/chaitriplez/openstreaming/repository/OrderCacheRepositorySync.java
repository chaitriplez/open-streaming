package com.github.chaitriplez.openstreaming.repository;

import java.util.List;
import java.util.Optional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Setter
@Component
public class OrderCacheRepositorySync implements OrderCacheRepository {

  @Autowired private OrderCacheRepository orderCacheRepository;

  @Override
  public synchronized List<OrderCache> findBySymbolAndActiveIsTrue(String symbol) {
    return orderCacheRepository.findBySymbolAndActiveIsTrue(symbol);
  }

  @Override
  public synchronized List<OrderCache> findByActiveIsTrue() {
    return orderCacheRepository.findByActiveIsTrue();
  }

  @Override
  public synchronized <S extends OrderCache> S save(S entity) {
    return orderCacheRepository.save(entity);
  }

  @Override
  public synchronized <S extends OrderCache> Iterable<S> saveAll(Iterable<S> entities) {
    return orderCacheRepository.saveAll(entities);
  }

  @Override
  public synchronized Optional<OrderCache> findById(Long aLong) {
    return orderCacheRepository.findById(aLong);
  }

  @Override
  public synchronized boolean existsById(Long aLong) {
    return orderCacheRepository.existsById(aLong);
  }

  @Override
  public synchronized Iterable<OrderCache> findAll() {
    return orderCacheRepository.findAll();
  }

  @Override
  public synchronized Iterable<OrderCache> findAllById(Iterable<Long> longs) {
    return orderCacheRepository.findAllById(longs);
  }

  @Override
  public synchronized long count() {
    return orderCacheRepository.count();
  }

  @Override
  public synchronized void deleteById(Long aLong) {
    orderCacheRepository.deleteById(aLong);
  }

  @Override
  public synchronized void delete(OrderCache entity) {
    orderCacheRepository.delete(entity);
  }

  @Override
  public synchronized void deleteAll(Iterable<? extends OrderCache> entities) {
    orderCacheRepository.deleteAll(entities);
  }

  @Override
  public synchronized void deleteAll() {
    orderCacheRepository.deleteAll();
  }
}
