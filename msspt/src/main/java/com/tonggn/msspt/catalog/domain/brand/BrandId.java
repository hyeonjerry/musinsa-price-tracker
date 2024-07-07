package com.tonggn.msspt.catalog.domain.brand;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode(of = "value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandId implements Serializable {

  @Column(name = "brand_id", length = 50, nullable = false)
  private String value;

  public BrandId(final String value) {
    this.value = value;
  }
}
