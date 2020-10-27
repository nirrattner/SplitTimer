package com.nirrattner.splittimer.models;

import com.nirrattner.splittimer.models.style.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Optional;

@ImmutableStyle
@Value.Immutable
public interface SplitConfigurationIF {
  String getName();
  Optional<Long> getBestValue();
  Optional<Long> getBestRunValue();
}
