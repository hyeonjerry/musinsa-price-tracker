package com.tonggn.msspt.catalog.application;

import com.tonggn.msspt.catalog.domain.category.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryQueryService {

  private final CategoryRepository categoryRepository;

  public List<CategoryResponse> getCategories() {
    return categoryRepository.findAll()
        .stream()
        .map(CategoryResponse::from)
        .toList();
  }
}
