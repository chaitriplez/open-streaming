package com.github.chaitriplez.openstreaming.service;

public interface PushInfoService {

  void connect() throws Exception;

  void syncOrderCache() throws Exception;
}
