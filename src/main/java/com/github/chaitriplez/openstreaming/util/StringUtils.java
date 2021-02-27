package com.github.chaitriplez.openstreaming.util;

public class StringUtils {

  public static String right(String s, int size) {
    return right(s, size, ' ');
  }

  public static String center(String s, int size) {
    return center(s, size, ' ');
  }

  public static String left(String s, int size) {
    return left(s, size, ' ');
  }

  public static String right(String s, int size, char pad) {
    if (s == null || size <= s.length()) return s;

    StringBuilder sb = new StringBuilder(size);
    for (int i = 0; i < (size - s.length()); i++) {
      sb.append(pad);
    }
    sb.append(s);
    return sb.toString();
  }

  public static String center(String s, int size, char pad) {
    if (s == null || size <= s.length())
      return s;

    StringBuilder sb = new StringBuilder(size);
    for (int i = 0; i < (size - s.length() + 1) / 2; i++) {
      sb.append(pad);
    }
    sb.append(s);
    while (sb.length() < size) {
      sb.append(pad);
    }
    return sb.toString();
  }

  public static String left(String s, int size, char pad) {
    if (s == null || size <= s.length())
      return s;

    StringBuilder sb = new StringBuilder(size);
    sb.append(s);
    for (int i = 0; i < (size - s.length()); i++) {
      sb.append(pad);
    }
    return sb.toString();
  }
}
