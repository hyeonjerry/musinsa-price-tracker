package com.tonggn.msspt.catalog.command.domain.category;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

  @EmbeddedId
  private CategoryId id;

  @Column(nullable = false)
  private String name;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public Category(final CategoryId id, final String name) {
    this.id = id;
    this.name = name;
  }
}
