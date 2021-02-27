package com.github.chaitriplez.openstreaming.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolInfoRepository extends CrudRepository<SymbolInfo, String> {}
