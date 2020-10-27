package com.nirrattner.splittimer.models;

import com.nirrattner.splittimer.models.style.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Optional;

@ImmutableStyle
@Value.Immutable
public interface SplitIF {
  SplitConfiguration getConfiguration();
  Optional<Long> getValue();
}
