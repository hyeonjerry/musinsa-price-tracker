package com.tonggn.msspt.config.converter;

import com.tonggn.msspt.catalog.query.PeriodRequest;
import org.springframework.core.convert.converter.Converter;

public class PeriodRequestConverter implements Converter<String, PeriodRequest> {

  @Override
  public PeriodRequest convert(final String value) {
    return PeriodRequest.from(value);
  }
}
