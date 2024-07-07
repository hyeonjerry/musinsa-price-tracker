package com.tonggn.msspt.catalog.domain.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode(of = "value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryId {

  @Column(name = "category_id", length = 10, nullable = false)
  private String value;

  public CategoryId(final String value) {
    this.value = value;
  }
}
