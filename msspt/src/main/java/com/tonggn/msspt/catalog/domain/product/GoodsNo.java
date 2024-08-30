package com.tonggn.msspt.catalog.domain.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode(of = "value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsNo {

  @Column(name = "goods_no", nullable = false, unique = true)
  private Long value;

  public GoodsNo(final Long value) {
    this.value = value;
  }
}
