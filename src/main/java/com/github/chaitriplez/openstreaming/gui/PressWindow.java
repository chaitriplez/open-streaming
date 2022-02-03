package com.github.chaitriplez.openstreaming.gui;

import com.github.chaitriplez.openstreaming.util.StringUtils;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.GridLayout.Alignment;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowListenerAdapter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PressWindow extends BasicWindow {
  private String symbol;
  private int pxDigit;
  private BigDecimal previousPx;
  private BigDecimal currentPx;

  private BigDecimal selectedPx;
  private int selectedPosition = 0;
  private Mode selectedMode = Mode.NONE;

  private Label lSymbol;
  private Label lPx;
  private Label lChange;
  private Label lTicker;
  private Label lStatus;

  private final int MAX_TICKER = 70;
  private StringBuilder tickerData = new StringBuilder(MAX_TICKER);
  private int tickerPosition = 0;

  private final List<BigDecimal> spreads = new ArrayList<>();
  private final int MAX_ROW = 18;
  private int rowPosition = 0;
  private List<Label> lBuys = new ArrayList<>(MAX_ROW);
  private List<Label> lBids = new ArrayList<>(MAX_ROW);
  private List<PxLadder> lPrices = new ArrayList<>(MAX_ROW);
  private List<Label> lAsks = new ArrayList<>(MAX_ROW);
  private List<Label> lSells = new ArrayList<>(MAX_ROW);

  public PressWindow(String symbol) {
    super();
    this.symbol = symbol;
    setupComponents();
    setupListener();
    mockPxLadder();
  }

  private void setupComponents() {
    setTitle(" Press ");
    setHints(Arrays.asList(Hint.FULL_SCREEN));
    setTheme(MyTheme.WHITE);
    setComponent(new Panel(new BorderLayout()));

    Panel layoutPanel = (Panel) getComponent();
    Panel topPanel = new Panel();
    Panel centerPanel = new Panel();
    Panel bottomPanel = new Panel();

    layoutPanel.addComponent(topPanel, Location.TOP);
    layoutPanel.addComponent(centerPanel, Location.CENTER);
    layoutPanel.addComponent(bottomPanel, Location.BOTTOM);

    {
      topPanel.setLayoutManager(new GridLayout(3).setHorizontalSpacing(4));
      topPanel.addComponent(lSymbol = new Label("-"));
      topPanel.addComponent(
          lPx = new Label("-"),
          GridLayout.createLayoutData(Alignment.END, Alignment.BEGINNING, true, false));
      tickerData.append(StringUtils.left("", MAX_TICKER));
      topPanel.addComponent(
      lTicker = new Label(tickerData.toString()).setPreferredSize(new TerminalSize(MAX_TICKER/2, 2)),
          GridLayout.createLayoutData(Alignment.END, Alignment.BEGINNING, false, false, 1, 2));
      topPanel.addComponent(
          lChange = new Label("-"),
          GridLayout.createLayoutData(Alignment.END, Alignment.BEGINNING, true, false, 2, 1));
      renderCursorTicker();
    }

    {
      centerPanel.setLayoutManager(new GridLayout(5));
      centerPanel.addComponent(headerLabel("V-Buy"));
      centerPanel.addComponent(headerLabel("Bid"));
      centerPanel.addComponent(headerLabel("Price"));
      centerPanel.addComponent(headerLabel("Ask"));
      centerPanel.addComponent(headerLabel("V-Sell"));

      for(int i = 0; i < MAX_ROW; i++) {
        centerPanel.addComponent(new Label(""), GridLayout.createLayoutData(Alignment.CENTER, Alignment.BEGINNING, true, false));
        centerPanel.addComponent(new QtyLadder("").setBid(true).setQty(i%4 * 10000));
        centerPanel.addComponent(new PxLadder());
        centerPanel.addComponent(new QtyLadder("").setBid(false).setQty(i%4 * 10000));
        centerPanel.addComponent(new Label(""), GridLayout.createLayoutData(Alignment.CENTER, Alignment.BEGINNING, true, false));
      }

      for(int i = 0; i < MAX_ROW; i++) {
        lBuys.add((Label) centerPanel.getChildrenList().get(5 + 5 * i));
        lBids.add((Label) centerPanel.getChildrenList().get(6 + 5 * i));
        lPrices.add((PxLadder) centerPanel.getChildrenList().get(7 + 5 * i));
        lAsks.add((Label) centerPanel.getChildrenList().get(8 + 5 * i));
        lSells.add((Label) centerPanel.getChildrenList().get(9 + 5 * i));
      }
      setFocusedInteractable(lPrices.get(selectedPosition));
    }

    bottomPanel.addComponent(lStatus = new Label(""));
  }

  private Label headerLabel(String s) {
    return new Label(s).addStyle(SGR.BOLD).addStyle(SGR.UNDERLINE).setLayoutData(GridLayout.createLayoutData(Alignment.CENTER, Alignment.BEGINNING, true, false));
  }

  private void setupListener() {
    addWindowListener(
        new WindowListenerAdapter() {
          @Override
          public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
            super.onInput(basePane, keyStroke, deliverEvent);
            if (keyStroke.getKeyType() == KeyType.ArrowUp) {
              selectedMode = Mode.NONE;
              movePosition(-1);
            }
            if (keyStroke.getKeyType() == KeyType.ArrowDown) {
              selectedMode = Mode.NONE;
              movePosition(1);
            }
            if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
              if(selectedMode == Mode.BUY) {
                buy();
              } else {
                selectedMode = Mode.BUY;
              }
            }
            if (keyStroke.getKeyType() == KeyType.ArrowRight) {
              if(selectedMode == Mode.SELL) {
                sell();
              } else {
                selectedMode = Mode.SELL;
              }
            }
            if (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 'w') {

            }
            if (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 's') {

            }
            if (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 'q') {
              PressWindow.this.close();
            }
            showStatus();
          }
        });
  }

  private void showStatus() {
    lStatus.setText("Mode: " + selectedMode + ", px: " + selectedPx);
  }

  private void buy() {
    // TODO
  }

  private void sell() {
    // TODO
  }

  private synchronized void movePosition(int offset) {
    if(selectedPosition + offset >= MAX_ROW || selectedPosition + offset < 0) {
      return;
    }
    selectedPosition += offset;
    selectedPx = lPrices.get(selectedPosition).getPx();
    setFocusedInteractable(lPrices.get(selectedPosition));
  }

  private void mockPxLadder() {
    long price = 400;
    long spread = 2;
    for (PxLadder ladder: lPrices) {
      ladder.setRefPx(BigDecimal.valueOf(390), 2);
      ladder.setPx(BigDecimal.valueOf(price));
      price -= spread;
    }
  }
  private void renderCursorTicker() {
    tickerData.setCharAt(tickerPosition, '█');
    lTicker.setText(tickerData.toString());
  }

  public void renderPx() {
    BigDecimal changePx = currentPx.subtract(previousPx);
    BigDecimal pChange = changePx.divide(previousPx,4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100d));

    int sign = changePx.compareTo(BigDecimal.ZERO);
    DecimalFormat pxFmt = new DecimalFormat();
    pxFmt.setGroupingSize(3);
    pxFmt.setMinimumFractionDigits(pxDigit);
    pxFmt.setMaximumFractionDigits(pxDigit);

    DecimalFormat percentFmt = new DecimalFormat();
    percentFmt.setGroupingSize(3);
    percentFmt.setMinimumFractionDigits(0);
    percentFmt.setMaximumFractionDigits(2);

    if (sign == 0) {
      lSymbol.setText(symbol);
      lPx.setText(pxFmt.format(currentPx));
      lChange.setText(String.format("%s (%s%%)", pxFmt.format(changePx), percentFmt.format(pChange)));

      lSymbol.setForegroundColor(ANSI.YELLOW_BRIGHT);
      lPx.setForegroundColor(ANSI.YELLOW_BRIGHT);
      lChange.setForegroundColor(ANSI.YELLOW_BRIGHT);
    } else if (sign > 0) {
      lSymbol.setText(symbol);
      lPx.setText(pxFmt.format(currentPx));
      lChange.setText(String.format("+%s (+%s%%)", pxFmt.format(changePx), percentFmt.format(pChange)));

      lSymbol.setForegroundColor(ANSI.GREEN);
      lPx.setForegroundColor(ANSI.GREEN);
      lChange.setForegroundColor(ANSI.GREEN);
    } else {
      lSymbol.setText(symbol);
      lPx.setText(pxFmt.format(currentPx));
      lChange.setText(String.format("%s (%s%%)", pxFmt.format(changePx), percentFmt.format(pChange)));

      lSymbol.setForegroundColor(ANSI.RED);
      lPx.setForegroundColor(ANSI.RED);
      lChange.setForegroundColor(ANSI.RED);
    }
  }

  enum Mode {
    NONE, BUY, SELL;
  }

  public static void main(String[] args) throws Exception {
    Terminal terminal = new DefaultTerminalFactory().createTerminal();
    Screen screen = new TerminalScreen(terminal);
    screen.startScreen();

    MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
    gui.addWindowAndWait(new PressWindow("PTT"));
  }
}
