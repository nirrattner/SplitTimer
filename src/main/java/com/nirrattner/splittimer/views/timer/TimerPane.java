package com.nirrattner.splittimer.views.timer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nirrattner.splittimer.controllers.listeners.TimerStateListener;
import com.nirrattner.splittimer.models.TimerState;
import com.nirrattner.splittimer.models.TimerStateIF;
import com.nirrattner.splittimer.util.TimeFormatter;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.time.Clock;

@Singleton
public class TimerPane extends StackPane implements TimerStateListener {

  private static final float PANEL_WIDTH = 200;

  private final Clock clock;
  private final TimeFormatter timeFormatter;
  private final Label label;

  private TimerState state;

  @Inject
  public TimerPane(
      Clock clock,
      TimeFormatter timeFormatter) {
    this.clock = clock;
    this.timeFormatter = timeFormatter;
    this.label = new Label();

    this.label.getStyleClass().add("timer-label");

    StackPane labelPane = new StackPane(this.label);
    labelPane.setAlignment(Pos.BOTTOM_LEFT);

    this.setMinWidth(PANEL_WIDTH);
    this.setMaxWidth(PANEL_WIDTH);

    this.setAlignment(Pos.BOTTOM_RIGHT);
    this.getChildren().addAll(labelPane);
  }

  @Override
  public void setState(TimerState state) {
    this.state = state;
    render();
  }

  public void render() {
    String labelText = state.getState() == TimerStateIF.State.RUNNING
        ? timeFormatter.formatTimer(clock.millis() - state.getStartTime())
        : timeFormatter.formatTimer(state.getEndTime() - state.getStartTime());
    label.setText(labelText);
  }
}
