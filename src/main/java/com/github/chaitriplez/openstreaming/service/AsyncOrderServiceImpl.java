package com.github.chaitriplez.openstreaming.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chaitriplez.openstreaming.component.JobManager;
import com.github.chaitriplez.openstreaming.component.OrderExecutionWorker;
import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import com.github.chaitriplez.openstreaming.repository.OrderCacheRepository;
import com.github.chaitriplez.openstreaming.service.task.CancelOrderTask;
import com.github.chaitriplez.openstreaming.service.task.LimitOrderTask;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Setter
@Service
public class AsyncOrderServiceImpl implements AsyncOrderService {

  @Autowired private ObjectMapper mapper;
  @Autowired private JobManager jobManager;
  @Autowired private OrderExecutionWorker orderExecutionWorker;
  @Autowired private OrderCacheRepository orderCacheRepository;

  @Override
  public Long limitOrder(List<LimitOrderRequest> requests) {
    Tuple<Job, List<JobDetail>> tuple = jobManager.prepareJob(requests.size());
    Job job = tuple.getFirst();
    job.setRequestType(String.class.getCanonicalName());
    job.setRequest(requests.toString());

    for (int i = 0; i < tuple.getSecond().size(); i++) {
      JobDetail detail = tuple.getSecond().get(i);
      LimitOrderRequest request = requests.get(i);
      setRequest(detail, LimitOrderRequest.class, request);
    }

    jobManager.save(tuple);

    for (int i = 0; i < tuple.getSecond().size(); i++) {
      JobDetail detail = tuple.getSecond().get(i);
      LimitOrderRequest request = requests.get(i);
      orderExecutionWorker.submit(new LimitOrderTask(detail.getId(), request));
    }

    return tuple.getFirst().getId();
  }

  @Override
  public Long cancelAllOrder() {
    List<Long> orders =
        orderCacheRepository.findByActiveIsTrue().stream()
            .map(orderCache -> orderCache.getOrderNo())
            .collect(Collectors.toList());

    return doCancel(orders, "cancelAllOrder:" + orders.toString());
  }

  @Override
  public Long cancelOrderBySymbol(String symbol) {
    List<Long> orders =
        orderCacheRepository.findBySymbolAndActiveIsTrue(symbol).stream()
            .map(orderCache -> orderCache.getOrderNo())
            .collect(Collectors.toList());

    return doCancel(orders, "cancelOrderBySymbol:" + symbol + " orders:" + orders.toString());
  }

  private Long doCancel(List<Long> orders, String description) {
    Tuple<Job, List<JobDetail>> tuple = jobManager.prepareJob(orders.size());
    Job job = tuple.getFirst();
    job.setRequestType(String.class.getCanonicalName());
    job.setRequest(description);

    for (int i = 0; i < tuple.getSecond().size(); i++) {
      JobDetail detail = tuple.getSecond().get(i);
      Long request = orders.get(i);
      setRequest(detail, Long.class, request);
    }

    jobManager.save(tuple);

    for (int i = 0; i < tuple.getSecond().size(); i++) {
      JobDetail detail = tuple.getSecond().get(i);
      Long request = orders.get(i);
      orderExecutionWorker.submit(new CancelOrderTask(detail.getId(), request));
    }

    return tuple.getFirst().getId();
  }

  private <T> void setRequest(JobDetail detail, Class<T> clazz, T request) {
    try {
      detail.setRequestType(clazz.getCanonicalName());
      detail.setRequest(mapper.writeValueAsString(request));
    } catch (JsonProcessingException e) {
      detail.setRequestType(String.class.getCanonicalName());
      detail.setRequest(request.toString());
    }
  }
}
