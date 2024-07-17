package com.tonggn.msspt.catalog.domain.product;

import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.category.CategoryId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

  @Column(nullable = false)
  private Integer normalPrice;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
      CascadeType.MERGE})
  private List<PriceHistory> priceHistories = new ArrayList<>();

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

  public Product(final Long goodsNo, final String name, final Integer normalPrice,
      final String imageUrl, final BrandId brandId, final CategoryId category) {
    this.goodsNo = goodsNo;
    this.name = name;
    this.normalPrice = normalPrice;
    this.imageUrl = imageUrl;
    this.brandId = brandId;
    this.category = category;
  }

  public void addLastPriceIfNew(final int price) {
    if (isNewPrice(price)) {
      final PriceHistory priceHistory = new PriceHistory(this, price);
      priceHistories.add(priceHistory);
    }
  }

  private boolean isNewPrice(final int price) {
    return priceHistories.isEmpty()
        || priceHistories.get(priceHistories.size() - 1).getPrice() != price;
  }
}
