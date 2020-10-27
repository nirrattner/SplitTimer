package com.nirrattner.splittimer.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.TimeUnit;

@Singleton
public class TimeFormatter {

  private static final long HOURS_DIVISOR = TimeUnit.HOURS.toMillis(1);
  private static final long MINUTES_DIVISOR = TimeUnit.MINUTES.toMillis(1);
  private static final long MINUTES_MODULO = TimeUnit.HOURS.toMinutes(1);
  private static final long SECOND_DIVISOR = TimeUnit.SECONDS.toMillis(1);
  private static final long SECOND_MODULO = TimeUnit.MINUTES.toSeconds(1);
  private static final long TENTH_SECOND_DIVISOR = 100;
  private static final long TENTH_SECOND_MODULO = 10;

  @Inject
  public TimeFormatter() {
  }

  public String formatTimer(long timeMillis) {
    long hours = timeMillis / HOURS_DIVISOR;
    long minutes = (timeMillis / MINUTES_DIVISOR) % MINUTES_MODULO;
    long seconds =  (timeMillis / SECOND_DIVISOR) % SECOND_MODULO;
    long tenth_seconds = (timeMillis / TENTH_SECOND_DIVISOR) % TENTH_SECOND_MODULO;
    return String.format("%d:%02d:%02d.%d", hours, minutes, seconds, tenth_seconds);
  }

  public String formatSplit(long timeMillis) {
    String sign = timeMillis < 0
      ? "-"
      : "";
    long absoluteTimeMillis = Math.abs(timeMillis);
    long hours = absoluteTimeMillis / HOURS_DIVISOR;
    long minutes = (absoluteTimeMillis / MINUTES_DIVISOR) % MINUTES_MODULO;
    long seconds =  (absoluteTimeMillis / SECOND_DIVISOR) % SECOND_MODULO;
    long tenth_seconds = (absoluteTimeMillis / TENTH_SECOND_DIVISOR) % TENTH_SECOND_MODULO;
    if (hours > 0) {
      return String.format("%s%d:%02d:%02d.%d", sign, hours, minutes, seconds, tenth_seconds);
    } else if (minutes > 0) {
      return String.format("%s%d:%02d.%d", sign, minutes, seconds, tenth_seconds);
    } else {
      return String.format("%s%d.%d", sign, seconds, tenth_seconds);
    }
  }

  public String formatSplitDifference(long timeMillis) {
    String sign = timeMillis > 0
        ? "+"
        : "";
    return sign + formatSplit(timeMillis);
  }
}

