package com.nirrattner.splittimer.models;

import com.nirrattner.splittimer.models.style.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ImmutableStyle
@Value.Immutable
public interface SplitsStateIF {
  List<Split> getSplits();

  @Value.Derived
  default int getCurrentIndex() {
    return (int) getSplits()
        .stream()
        .filter(split -> split.getTimestamp().isPresent())
        .count();
  }

  @Value.Lazy
  default Optional<Long> getBestCompletedRunTime() {
    return getSplits().get(getSplits().size() - 1)
        .getConfiguration()
        .getBestRunTimestamp();
  }

  @Value.Lazy
  default boolean hasBestRunSplitsRemaining() {
    return getSplits().stream()
        .skip(getCurrentIndex())
        .map(Split::getConfiguration)
        .map(SplitConfiguration::getBestRunTimestamp)
        .anyMatch(Optional::isPresent);
  }
}
