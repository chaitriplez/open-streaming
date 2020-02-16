// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: infov1.proto

package com.settrade.openapi.protobuf.v1;

public interface InfoV1OrBuilder extends
    // @@protoc_insertion_point(interface_extends:settrade.openapi.protobuf.v1.InfoV1)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Subscribed symbol
   * </pre>
   *
   * <code>string symbol = 1;</code>
   * @return The symbol.
   */
  java.lang.String getSymbol();
  /**
   * <pre>
   * Subscribed symbol
   * </pre>
   *
   * <code>string symbol = 1;</code>
   * @return The bytes for symbol.
   */
  com.google.protobuf.ByteString
      getSymbolBytes();

  /**
   * <pre>
   * Highest price
   * </pre>
   *
   * <code>.google.type.Money high = 2;</code>
   * @return Whether the high field is set.
   */
  boolean hasHigh();
  /**
   * <pre>
   * Highest price
   * </pre>
   *
   * <code>.google.type.Money high = 2;</code>
   * @return The high.
   */
  com.google.type.Money getHigh();
  /**
   * <pre>
   * Highest price
   * </pre>
   *
   * <code>.google.type.Money high = 2;</code>
   */
  com.google.type.MoneyOrBuilder getHighOrBuilder();

  /**
   * <pre>
   * Lowest price
   * </pre>
   *
   * <code>.google.type.Money low = 3;</code>
   * @return Whether the low field is set.
   */
  boolean hasLow();
  /**
   * <pre>
   * Lowest price
   * </pre>
   *
   * <code>.google.type.Money low = 3;</code>
   * @return The low.
   */
  com.google.type.Money getLow();
  /**
   * <pre>
   * Lowest price
   * </pre>
   *
   * <code>.google.type.Money low = 3;</code>
   */
  com.google.type.MoneyOrBuilder getLowOrBuilder();

  /**
   * <pre>
   * Current price
   * </pre>
   *
   * <code>.google.type.Money last = 4;</code>
   * @return Whether the last field is set.
   */
  boolean hasLast();
  /**
   * <pre>
   * Current price
   * </pre>
   *
   * <code>.google.type.Money last = 4;</code>
   * @return The last.
   */
  com.google.type.Money getLast();
  /**
   * <pre>
   * Current price
   * </pre>
   *
   * <code>.google.type.Money last = 4;</code>
   */
  com.google.type.MoneyOrBuilder getLastOrBuilder();

  /**
   * <pre>
   * Total traded volume
   * </pre>
   *
   * <code>int64 total_volume = 5;</code>
   * @return The totalVolume.
   */
  long getTotalVolume();
}
