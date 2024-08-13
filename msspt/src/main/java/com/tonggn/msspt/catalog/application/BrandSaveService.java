package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.domain.brand.Brand;
import com.tonggn.msspt.catalog.domain.brand.BrandId;
import com.tonggn.msspt.catalog.domain.brand.BrandRepository;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandSaveService {

  private final BrandRepository brandRepository;

  public void saveOnlyNewBrands(final Collection<BrandSaveRequest> requests) {
    final List<Brand> brands = requests.stream()
        .map(this::mapToBrand)
        .toList();

    final List<Brand> newBrands = filterNewBrands(brands);

    brandRepository.saveAll(newBrands);
  }

  private List<Brand> filterNewBrands(final List<Brand> brands) {
    final List<BrandId> brandIds = brands.stream()
        .map(Brand::getId)
        .toList();
    final List<Brand> existingBrands = brandRepository.findAllByIdIn(brandIds);
    return brands.stream()
        .filter(brand -> !existingBrands.contains(brand))
        .toList();
  }

  private Brand mapToBrand(final BrandSaveRequest request) {
    final BrandId brandId = new BrandId(request.id());
    return new Brand(brandId, request.name(), request.englishName());
  }
}
