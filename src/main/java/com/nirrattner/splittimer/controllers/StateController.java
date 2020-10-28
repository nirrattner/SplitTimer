package com.nirrattner.splittimer.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
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
                .withTimestamp(now - timerState.getStartTime()));
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
                          .map(split -> split.withTimestamp(Optional.empty()))
                          .collect(Collectors.toUnmodifiableList()));
              notifyListeners();
            });
  }

  public void load(File file) {
    fileState = FileState.builder()
        .setFile(file)
        .build();
    try {
      List<SplitConfiguration> configurations = objectMapper.readValue(
          file,
          new TypeReference<>() {});
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
      boolean isBestRun = splitsState.getBestCompletedRunTime()
          .map(bestCompletedRunTime -> timerState.getState() == TimerStateIF.State.DONE
              && (timerState.getEndTime() - timerState.getStartTime()) < bestCompletedRunTime)
          .orElse(!splitsState.hasBestRunSplitsRemaining());
      ImmutableList.Builder<SplitConfiguration> configurations = ImmutableList.builder();
      for (int i = 0; i < splitsState.getSplits().size(); i++) {
        configurations.add(
            toSplitConfiguration(
                splitsState.getSplits().get(i),
                i > 0
                    ? splitsState.getSplits().get(i - 1).getTimestamp()
                    : Optional.of(0L),
                isBestRun));
      }
      try {
        objectMapper.writeValue(file, configurations.build());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private SplitConfiguration toSplitConfiguration(
      Split split,
      Optional<Long> previousSplitTimeStamp,
      boolean isBestRun) {
    SplitConfiguration.Builder splitConfiguration = SplitConfiguration.builder()
        .from(split.getConfiguration());
    split.getTimestamp()
        .ifPresent(value -> {
          if (isBestRun) {
            splitConfiguration.setBestRunTimestamp(split.getTimestamp());
          }
          if (value - previousSplitTimeStamp.get() < split.getConfiguration().getBestTime().orElse(Long.MAX_VALUE)) {
            splitConfiguration.setBestTime(value - previousSplitTimeStamp.get());
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
