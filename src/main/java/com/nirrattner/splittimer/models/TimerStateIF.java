package com.nirrattner.splittimer.models;

import com.nirrattner.splittimer.models.style.ImmutableStyle;
import org.immutables.value.Value;

@ImmutableStyle
@Value.Immutable
public interface TimerStateIF {
  enum State {
    UNINITIALIZED,
    READY,
    RUNNING,
    DONE,
  }

  @Value.Default
  default State getState() {
    return State.UNINITIALIZED;
  }

  @Value.Default
  default long getStartTime() {
    return 0;
  }

  @Value.Default
  default long getEndTime() {
    return 0;
  }
}
