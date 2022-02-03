package com.github.chaitriplez.openstreaming.gui;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;

public class MyTheme {
  public static final Theme WHITE = SimpleTheme.makeTheme(true,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.BLACK);

  public static final Theme YELLOW = SimpleTheme.makeTheme(false,
      ANSI.YELLOW_BRIGHT,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.BLACK);

  public static final Theme RED = SimpleTheme.makeTheme(false,
      ANSI.RED_BRIGHT,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.BLACK);

  public static final Theme GREEN = SimpleTheme.makeTheme(false,
      ANSI.GREEN_BRIGHT,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.WHITE,
      ANSI.BLACK,
      ANSI.BLACK);
}
