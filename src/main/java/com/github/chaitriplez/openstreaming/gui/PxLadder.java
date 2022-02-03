package com.github.chaitriplez.openstreaming.gui;

import static com.github.chaitriplez.openstreaming.gui.MyTheme.GREEN;
import static com.github.chaitriplez.openstreaming.gui.MyTheme.RED;
import static com.github.chaitriplez.openstreaming.gui.MyTheme.WHITE;
import static com.github.chaitriplez.openstreaming.gui.MyTheme.YELLOW;

import com.github.chaitriplez.openstreaming.util.StringUtils;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.input.KeyStroke;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class PxLadder extends Button {

  private int defaultWidth = 15;
  private final DecimalFormat fmt = new DecimalFormat();
  private BigDecimal refPx;
  private int pxDigit;
  private BigDecimal px;
  private boolean lastPx;

  public PxLadder() {
    super("");
    setLayoutData(GridLayout.createLayoutData(Alignment.CENTER, Alignment.BEGINNING, true, false));
    setRenderer(new FlatButtonRenderer());
    setTheme(WHITE);
  }

  public PxLadder setRefPx(BigDecimal previousPx, int pxDigit) {
    this.refPx = previousPx;
    this.pxDigit = pxDigit;
    fmt.setGroupingSize(3);
    fmt.setMinimumFractionDigits(pxDigit);
    fmt.setMaximumFractionDigits(pxDigit);
    renderPx();
    return this;
  }

  @Override
  public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
    return Result.UNHANDLED;
  }

  public PxLadder setPx(BigDecimal px) {
    this.px = px;
    renderPx();
    return this;
  }

  public BigDecimal getPx() {
    return px;
  }

  public void setLastPx(boolean lastPx) {
    lastPx = lastPx;
    renderPx();
  }

  public boolean isLastPx() {
    return lastPx;
  }

  private void renderPx() {
    if(refPx != null && px != null) {
      setLabel((StringUtils.center(fmt.format(px), defaultWidth, lastPx?'░':' ')));
      int sign = px.compareTo(refPx);
      if(sign > 0) {
        setTheme(GREEN);
      } else if(sign < 0) {
        setTheme(RED);
      } else {
        setTheme(YELLOW);
      }
    } else {
      setTheme(WHITE);
    }
  }
}
