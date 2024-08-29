package com.tonggn.msspt.catalog.domain.product;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private Long goodsNo;

  @Column(nullable = false)
  private String name;

  @Embedded
  private PriceDetails priceDetails;

  @Column(nullable = false)
  private String imageUrl;

  @Embedded
  private BrandId brandId;

  @Embedded
  private CategoryId category;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public Product(final Long goodsNo, final String name, final Integer price, final String imageUrl,
      final BrandId brandId, final CategoryId category) {
    this.goodsNo = goodsNo;
    this.name = name;
    this.priceDetails = new PriceDetails(price);
    this.imageUrl = imageUrl;
    this.brandId = brandId;
    this.category = category;
  }

  public List<PriceHistory> getPriceHistories() {
    return priceDetails.getPriceHistories();
  }

  public void addLastPrice(final int price) {
    priceDetails.addLastPrice(this, price);
  }
}
