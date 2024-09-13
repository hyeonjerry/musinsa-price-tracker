package com.tonggn.msspt.catalog.command.domain.brand;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, BrandId> {

  List<Brand> findAllByIdIn(List<BrandId> brandIds);
}
