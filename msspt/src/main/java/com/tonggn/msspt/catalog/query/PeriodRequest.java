package com.tonggn.msspt.catalog.query;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PeriodRequest {
  DAILY("daily"),
  WEEKLY("weekly"),
  MONTHLY("monthly");

  private final String value;

  public static PeriodRequest from(final String value) {
    return Arrays.stream(values())
        .filter(period -> period.value.equals(value))
        .findFirst()
        .orElse(WEEKLY);
  }
}
