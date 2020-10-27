package com.nirrattner.splittimer.models;

import com.nirrattner.splittimer.models.style.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@ImmutableStyle
@Value.Immutable
public interface SplitsStateIF {
  List<Split> getSplits();

  @Value.Derived
  default int getCurrentIndex() {
    return (int) getSplits()
        .stream()
        .filter(split -> split.getValue().isPresent())
        .count();
  }

  @Value.Lazy
  default long getBestRunTime() {
    return getSplits().stream()
        .map(Split::getConfiguration)
        .map(SplitConfiguration::getBestRunValue)
        .flatMap(Optional::stream)
        .reduce(0L, Long::sum);
  }
}
