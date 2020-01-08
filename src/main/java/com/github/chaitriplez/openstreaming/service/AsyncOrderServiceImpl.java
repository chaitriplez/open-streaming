package com.github.chaitriplez.openstreaming.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chaitriplez.openstreaming.api.Position;
import com.github.chaitriplez.openstreaming.component.JobManager;
import com.github.chaitriplez.openstreaming.component.OrderExecution;
import com.github.chaitriplez.openstreaming.component.OrderExecutionWorker;
import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import com.github.chaitriplez.openstreaming.repository.OrderCacheRepository;
import com.github.chaitriplez.openstreaming.service.task.CancelOrderTask;
import com.github.chaitriplez.openstreaming.service.task.ChangePxQtyTask;
import com.github.chaitriplez.openstreaming.service.task.EmptyTask;
import com.github.chaitriplez.openstreaming.service.task.LimitOrderTask;
import com.github.chaitriplez.openstreaming.service.task.OrderCacheCondition;
import com.github.chaitriplez.openstreaming.service.task.QuoteChangeTask;
import com.github.chaitriplez.openstreaming.service.task.QuotePlaceTask;
import com.github.chaitriplez.openstreaming.util.QuoteCalculator;
import com.github.chaitriplez.openstreaming.util.QuoteCalculator.Command;
import com.github.chaitriplez.openstreaming.util.QuoteCalculator.NewQuote;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
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
            .map(OrderCache::getOrderNo)
            .collect(Collectors.toList());

    return doCancel(orders, "cancelAllOrder:" + orders.toString());
  }

  @Override
  public Long cancelOrderBySymbol(String symbol) {
    List<Long> orders =
        orderCacheRepository.findBySymbolAndActiveIsTrue(symbol).stream()
            .map(OrderCache::getOrderNo)
            .collect(Collectors.toList());

    return doCancel(orders, "cancelOrderBySymbol:" + symbol + " orders:" + orders.toString());
  }

  @Override
  public Long changePxQty(List<ChangePxQtyRequest> requests) {
    final Tuple<Job, List<JobDetail>> tuple = jobManager.prepareJob(requests.size());
    final Job job = tuple.getFirst();
    job.setRequestType(String.class.getCanonicalName());
    job.setRequest(requests.toString());

    for (int i = 0; i < tuple.getSecond().size(); i++) {
      final JobDetail detail = tuple.getSecond().get(i);
      final ChangePxQtyRequest request = requests.get(i);
      setRequest(detail, ChangePxQtyRequest.class, request);
    }
    jobManager.save(tuple);

    for (int i = 0; i < tuple.getSecond().size(); i++) {
      final JobDetail detail = tuple.getSecond().get(i);
      final ChangePxQtyRequest request = requests.get(i);
      orderExecutionWorker.submit(new ChangePxQtyTask(detail.getId(), request));
    }

    return tuple.getFirst().getId();
  }

  @Override
  public Long quote(String symbol, List<QuoteRequest> quotes) {
    // TODO validate input

    final List<OrderCache> caches = orderCacheRepository.findBySymbolAndActiveIsTrue(symbol);
    // TODO validate current order

    final Map<Long, OrderCache> cacheMap = new HashMap<>();
    for (OrderCache cache : caches) {
      cacheMap.put(cache.getOrderNo(), cache);
    }

    // Initial quote calculator
    final QuoteCalculator calculator = new QuoteCalculator();
    for (OrderCache cache : caches) {
      calculator.addCurrentQuote(cache);
    }
    for (QuoteRequest quote : quotes) {
      calculator.addNewQuote(new NewQuote(quote.getSide(), quote.getPx(), quote.getQty()));
    }

    // Prevent wash sell
    final List<OrderCacheCondition> conditions = new ArrayList<>();
    for (Long orderNo : calculator.possibleWashSellOrder()) {
      final OrderCache cache = cacheMap.get(orderNo);
      final BigDecimal currentPx = cache.getPx();
      final Predicate<OrderCache> predicate =
          o -> o.getBalanceQty() == 0 || (o.getPx().compareTo(currentPx) != 0);

      conditions.add(new OrderCacheCondition(orderNo, predicate));
    }

    // Create task
    final List<Command> commands = calculator.getResult();
    final Tuple<Job, List<JobDetail>> tuple = jobManager.prepareJob(commands.size());
    final Job job = tuple.getFirst();
    job.setRequestType(String.class.getCanonicalName());
    job.setRequest(symbol + ": " + quotes);

    final List<OrderExecution> tasks = new ArrayList<>();
    for (int i = 0; i < commands.size(); i++) {
      final Command command = commands.get(i);
      final JobDetail jobDetail = tuple.getSecond().get(i);

      switch (command.getAction()) {
        case CANCEL:
          {
            tasks.add(new CancelOrderTask(jobDetail.getId(), command.getOrderNo()));
            setRequest(jobDetail, Long.class, command.getOrderNo());
          }
          break;
        case PX_PRIORITY_LOWER:
        case PX_PRIORITY_EQUAL:
          {
            if (command.isChangePx() || command.isChangeQty()) {
              final ChangePxQtyRequest request = toChangeOrder(command);

              tasks.add(new ChangePxQtyTask(jobDetail.getId(), toChangeOrder(command)));
              setRequest(jobDetail, ChangePxQtyRequest.class, request);
            } else {
              tasks.add(new EmptyTask(jobDetail.getId()));
              setRequest(jobDetail, Long.class, command.getOrderNo());
            }
          }
          break;
        case PX_PRIORITY_HIGHER:
          {
            final ChangePxQtyRequest request = toChangeOrder(command);

            tasks.add(new QuoteChangeTask(jobDetail.getId(), new Tuple<>(conditions, request)));
            setRequest(jobDetail, ChangePxQtyRequest.class, request);
          }
          break;
        case PLACE:
          {
            final LimitOrderRequest request = toPlaceOrder(symbol, command);

            tasks.add(new QuotePlaceTask(jobDetail.getId(), new Tuple<>(conditions, request)));
            setRequest(jobDetail, LimitOrderRequest.class, request);
          }
          break;
      }
    }

    // Submit task
    jobManager.save(tuple);
    tasks.forEach(execution -> orderExecutionWorker.submit(execution));

    return tuple.getFirst().getId();
  }

  private LimitOrderRequest toPlaceOrder(String symbol, Command command) {
    LimitOrderRequest request = new LimitOrderRequest();
    request.setPosition(Position.OPEN);
    request.setSide(command.getSide());
    request.setSymbol(symbol);
    request.setPx(command.getPx());
    request.setQty(command.getQty());
    return request;
  }

  private ChangePxQtyRequest toChangeOrder(Command command) {
    ChangePxQtyRequest request = new ChangePxQtyRequest();
    request.setOrderNo(command.getOrderNo());
    request.setChangePx(command.isChangePx());
    request.setPx(command.getPx());
    request.setChangeQty(command.isChangeQty());
    request.setQty(command.getQty());
    return request;
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
