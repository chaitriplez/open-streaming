package com.github.chaitriplez.openstreaming.gui;

import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;
import com.googlecode.lanterna.gui2.Label;
import java.text.DecimalFormat;

public class QtyLadder extends Label {

  private long qty = 0;
  private boolean bid = false;

  public QtyLadder(String text) {
    super(text);
    setLayoutData(GridLayout.createLayoutData(Alignment.CENTER, Alignment.BEGINNING, true, false));
  }

  public QtyLadder setBid(boolean bid) {
    this.bid = bid;
    return this;
  }

  public QtyLadder setQty(long qty) {
    this.qty = qty;
    if(qty == 0) {
      setText("");
    } else {
      setText(String.format("%,d", qty));
    }
    return this;
  }
}
