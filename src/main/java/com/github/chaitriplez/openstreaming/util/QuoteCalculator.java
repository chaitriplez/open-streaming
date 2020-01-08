package com.github.chaitriplez.openstreaming.util;

import com.github.chaitriplez.openstreaming.api.LongShort;
import com.github.chaitriplez.openstreaming.repository.OrderCache;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class QuoteCalculator {

  private static final int MAX_DIGIT = 6;
  private Map<Long, NewQuote> newQuotes = new HashMap<>();
  private Map<Long, CurrentQuote> currentQuotes = new HashMap<>();

  public void addCurrentQuote(OrderCache cache) {
    CurrentQuote value = from(cache);

    if (currentQuotes.putIfAbsent(createKey(value.getPx()), value) != null) {
      throw new IllegalArgumentException("Not support multiple quote at the same price: " + value);
    }
  }

  private CurrentQuote from(OrderCache cache) {
    CurrentQuote value = new CurrentQuote();
    value.setOrderNo(cache.getOrderNo());
    value.setSide(LongShort.valueOf(cache.getSide()));
    value.setPx(cache.getPx());
    value.setQty(cache.getQty());
    value.setBalanceQty(cache.getBalanceQty());
    return value;
  }

  public void addCurrentQuote(CurrentQuote value) {
    if (currentQuotes.putIfAbsent(createKey(value.getPx()), value) != null) {
      throw new IllegalArgumentException("Not support multiple quote at the same price: " + value);
    }
  }

  public void addNewQuote(NewQuote value) {
    if (newQuotes.putIfAbsent(createKey(value.getPx()), value) != null) {
      throw new IllegalArgumentException("Not support multiple quote at the same price: " + value);
    }
  }

  public List<Command> getResult() {
    final List<Command> result = new ArrayList<>();
    result.addAll(calculate(LongShort.LONG));
    result.addAll(calculate(LongShort.SHORT));
    result.sort(Comparator.comparing(o -> o.action));

    return result;
  }

  public Set<Long> possibleWashSellOrder() {
    final Set<Long> orderNos = new HashSet<>();
    for (NewQuote newQuote : newQuotes.values()) {
      for (CurrentQuote currentQuote : currentQuotes.values()) {
        if (newQuote.getSide() != currentQuote.getSide()) {
          if (newQuote.getSide() == LongShort.LONG) {
            if (newQuote.getPx().compareTo(currentQuote.getPx()) >= 0) {
              orderNos.add(currentQuote.getOrderNo());
            }
          } else {
            if (newQuote.getPx().compareTo(currentQuote.getPx()) <= 0) {
              orderNos.add(currentQuote.getOrderNo());
            }
          }
        }
      }
    }
    return orderNos;
  }

  private Long createKey(BigDecimal k) {
    return k.movePointRight(MAX_DIGIT).longValue();
  }

  private List<Command> calculate(LongShort side) {
    final List<Command> result = new ArrayList<>();

    final List<Long> newPxQuotes =
        newQuotes.entrySet().stream()
            .filter(e -> e.getValue().side == side)
            .map(e -> e.getKey())
            .collect(Collectors.toList());

    final List<Long> currentPxQuotes =
        currentQuotes.entrySet().stream()
            .filter(e -> e.getValue().side == side)
            .map(e -> e.getKey())
            .collect(Collectors.toList());

    final List<Long> intersectPxs = new ArrayList<>(newPxQuotes);
    intersectPxs.retainAll(currentPxQuotes);
    for (Long px : intersectPxs) {
      final CurrentQuote currentQuote = currentQuotes.get(px);
      final NewQuote newQuote = newQuotes.get(px);
      final Command command = changeQty(currentQuote, newQuote);
      result.add(command);
    }

    currentPxQuotes.removeAll(intersectPxs);
    Collections.sort(currentPxQuotes);

    newPxQuotes.removeAll(intersectPxs);
    Collections.sort(newPxQuotes);

    if (currentPxQuotes.size() >= newPxQuotes.size()) {
      for (int i = 0; i < currentPxQuotes.size(); i++) {
        final CurrentQuote currentQuote = currentQuotes.get(currentPxQuotes.get(i));
        if (i < newPxQuotes.size()) {
          final NewQuote newQuote = newQuotes.get(newPxQuotes.get(i));
          final Command command = changePxQty(newQuote, currentQuote);
          result.add(command);
        } else {
          result.add(
              Command.builder().action(Action.CANCEL).orderNo(currentQuote.getOrderNo()).build());
        }
      }
    } else {
      for (int i = 0; i < newPxQuotes.size(); i++) {
        final NewQuote newQuote = newQuotes.get(newPxQuotes.get(i));
        if (i < currentPxQuotes.size()) {
          final CurrentQuote currentQuote = currentQuotes.get(currentPxQuotes.get(i));
          final Command command = changePxQty(newQuote, currentQuote);
          result.add(command);
        } else {
          result.add(
              Command.builder()
                  .action(Action.PLACE)
                  .side(newQuote.getSide())
                  .px(newQuote.getPx())
                  .qty(newQuote.getQty())
                  .build());
        }
      }
    }
    return result;
  }

  private Command changeQty(CurrentQuote currentQuote, NewQuote newQuote) {
    final boolean changeQty = currentQuote.getBalanceQty() != newQuote.getQty();
    final int newQty = currentQuote.getQty() + (newQuote.getQty() - currentQuote.getBalanceQty());
    return Command.builder()
        .action(Action.PX_PRIORITY_EQUAL)
        .orderNo(currentQuote.getOrderNo())
        .changePx(false)
        .px(newQuote.getPx())
        .changeQty(changeQty)
        .qty(newQty)
        .build();
  }

  private Command changePxQty(NewQuote newQuote, CurrentQuote currentQuote) {
    final Action action;
    if (currentQuote.getSide() == LongShort.LONG) {
      action =
          newQuote.getPx().compareTo(currentQuote.getPx()) < 0
              ? Action.PX_PRIORITY_LOWER
              : Action.PX_PRIORITY_HIGHER;
    } else {
      action =
          newQuote.getPx().compareTo(currentQuote.getPx()) > 0
              ? Action.PX_PRIORITY_LOWER
              : Action.PX_PRIORITY_HIGHER;
    }
    final boolean changeQty = currentQuote.getBalanceQty() != newQuote.getQty();
    final int newQty = currentQuote.getQty() + (newQuote.getQty() - currentQuote.getBalanceQty());
    return Command.builder()
        .action(action)
        .orderNo(currentQuote.getOrderNo())
        .changePx(true)
        .px(newQuote.getPx())
        .changeQty(changeQty)
        .qty(newQty)
        .build();
  }

  public enum Action {
    CANCEL,
    PX_PRIORITY_LOWER,
    PX_PRIORITY_EQUAL,
    PX_PRIORITY_HIGHER,
    PLACE
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NewQuote {
    private LongShort side;
    private BigDecimal px;
    private Integer qty;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CurrentQuote {
    private Long orderNo;
    private LongShort side;
    private BigDecimal px;
    private Integer qty;
    private Integer balanceQty;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Command {
    private Action action;
    private LongShort side;
    private BigDecimal px;
    private Integer qty;
    private Long orderNo;
    private boolean changePx;
    private boolean changeQty;
  }
}
