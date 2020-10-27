package com.nirrattner.splittimer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.nirrattner.splittimer.controllers.listeners.FileStateListener;
import com.nirrattner.splittimer.controllers.listeners.SplitsStateListener;
import com.nirrattner.splittimer.controllers.listeners.TimerStateListener;
import com.nirrattner.splittimer.views.SplitTimerMenuBar;
import com.nirrattner.splittimer.views.timer.TimersAnimationTimer;
import com.nirrattner.splittimer.views.splits.SplitsPane;
import com.nirrattner.splittimer.views.timer.TimerPane;
import javafx.stage.Stage;

import java.time.Clock;

public class SplitTimerModule extends AbstractModule {

  private final Stage stage;

  public SplitTimerModule(Stage stage) {
    this.stage = stage;
  }

  @Override
  protected void configure() {
    bind(Clock.class).toInstance(Clock.systemUTC());

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module().configureAbsentsAsNulls(true));
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    bind(ObjectMapper.class).toInstance(objectMapper);

    bind(Stage.class).toInstance(stage);

    Multibinder<FileStateListener> fileStateListenerBinder = Multibinder.newSetBinder(
        binder(),
        FileStateListener.class);
    fileStateListenerBinder.addBinding().to(SplitTimerMenuBar.class);

    Multibinder<SplitsStateListener> splitsStateListenerBinder = Multibinder.newSetBinder(
        binder(),
        SplitsStateListener.class);
    splitsStateListenerBinder.addBinding().to(SplitsPane.class);

    Multibinder<TimerStateListener> timerStateListenerBinder = Multibinder.newSetBinder(
        binder(),
        TimerStateListener.class);
    timerStateListenerBinder.addBinding().to(TimerPane.class);
    timerStateListenerBinder.addBinding().to(TimersAnimationTimer.class);

  }
}
