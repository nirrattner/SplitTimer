package com.nirrattner.splittimer.views.timer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nirrattner.splittimer.controllers.listeners.TimerStateListener;
import com.nirrattner.splittimer.models.TimerState;
import javafx.animation.AnimationTimer;

@Singleton
public class TimersAnimationTimer extends AnimationTimer implements TimerStateListener {

  private final TimerPane timerPane;

  @Inject
  public TimersAnimationTimer(TimerPane timerPane) {
    this.timerPane = timerPane;
  }

  @Override
  public void handle(long now) {
    timerPane.render();
  }

  @Override
  public void setState(TimerState state) {
    switch (state.getState()) {
      case RUNNING:
        start();
        break;
      default:
        stop();
        break;
    }
  }
}
