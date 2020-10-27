package com.nirrattner.splittimer.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nirrattner.splittimer.controllers.listeners.FileStateListener;
import com.nirrattner.splittimer.controllers.listeners.SplitsStateListener;
import com.nirrattner.splittimer.controllers.listeners.TimerStateListener;
import com.nirrattner.splittimer.models.FileState;
import com.nirrattner.splittimer.models.Split;
import com.nirrattner.splittimer.models.SplitConfiguration;
import com.nirrattner.splittimer.models.SplitsState;
import com.nirrattner.splittimer.models.TimerState;
import com.nirrattner.splittimer.models.TimerStateIF;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class StateController {

  private final Clock clock;
  private final ObjectMapper objectMapper;
  private final Set<FileStateListener> fileStateListeners;
  private final Set<SplitsStateListener> splitsStateListeners;
  private final Set<TimerStateListener> timerStateListeners;

  private FileState fileState;
  private SplitsState splitsState;
  private TimerState timerState;

  @Inject
  public StateController(
      Clock clock,
      ObjectMapper objectMapper,
      Set<FileStateListener> fileStateListeners,
      Set<SplitsStateListener> splitsStateListeners,
      Set<TimerStateListener> timerStateListeners) {
    this.clock = clock;
    this.objectMapper = objectMapper;
    this.fileStateListeners = fileStateListeners;
    this.splitsStateListeners = splitsStateListeners;
    this.timerStateListeners = timerStateListeners;

    this.fileState = FileState.builder().build();
    this.timerState = TimerState.builder().build();
    this.splitsState = SplitsState.builder().build();

    notifyListeners();
  }

  public void split() {
    long now = clock.millis();
    switch (timerState.getState()) {
      case READY:
        timerState = timerState
            .withState(TimerStateIF.State.RUNNING)
            .withStartTime(now);
        break;
      case RUNNING:
        List<Split> splits = new ArrayList<>(splitsState.getSplits());
        splits.set(
            splitsState.getCurrentIndex(),
            splitsState.getSplits().get(splitsState.getCurrentIndex())
                .withValue(now - timerState.getStartTime()));
        splitsState = splitsState.withSplits(splits);
        if (splitsState.getCurrentIndex() == splitsState.getSplits().size()) {
          timerState = timerState
              .withState(TimerStateIF.State.DONE)
              .withEndTime(now);
          save();
        }
        break;
    }

    notifyListeners();
  }

  public void reset() {
    fileState.getFile()
        .ifPresentOrElse(
            this::load,
            () -> {
              timerState = TimerState.builder().build();
              splitsState = splitsState
                  .withSplits(
                      splitsState.getSplits().stream()
                          .map(split -> split.withValue(Optional.empty()))
                          .collect(Collectors.toUnmodifiableList()));
              notifyListeners();
            });
  }

  public void load(File file) {
    fileState = FileState.builder()
        .setFile(file)
        .build();
    try {
      List<SplitConfiguration> configurations = objectMapper.readValue(file, new TypeReference<>() {});
      this.splitsState = SplitsState.builder()
          .addAllSplits(
              configurations.stream()
                  .map(
                      configuration -> Split.builder()
                          .setConfiguration(configuration)
                          .build())
                  .collect(Collectors.toUnmodifiableList()))
          .build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    timerState = TimerState.builder()
        .setState(TimerStateIF.State.READY)
        .build();

    notifyListeners();
  }

  public void save() {
    fileState.getFile().ifPresent(file -> {
      boolean isBestRun = timerState.getState() == TimerStateIF.State.DONE
          && (timerState.getEndTime() - timerState.getStartTime()) < splitsState.getBestRunTime();
      List<SplitConfiguration> configurations = splitsState.getSplits().stream()
          .map(split -> toSplitConfiguration(split, isBestRun))
          .collect(Collectors.toUnmodifiableList());
      try {
        objectMapper.writeValue(file, configurations);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private SplitConfiguration toSplitConfiguration(Split split, boolean isBestRun) {
    SplitConfiguration.Builder splitConfiguration = SplitConfiguration.builder()
        .from(split.getConfiguration());
    split.getValue()
        .ifPresent(value -> {
          if (isBestRun) {
            splitConfiguration.setBestRunValue(split.getValue());
          }
          if (value < split.getConfiguration().getBestValue().orElse(Long.MAX_VALUE)) {
            splitConfiguration.setBestValue(value);
          }
        });
    return splitConfiguration.build();
  }

  private void notifyListeners() {
    fileStateListeners.forEach(listener -> listener.setState(fileState));
    splitsStateListeners.forEach(listener -> listener.setState(splitsState));
    timerStateListeners.forEach(listener -> listener.setState(timerState));
  }
}
