package com.tonggn.msspt.catalog.command.domain.category;

import java.util.List;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Category, Long> {

  List<Category> findAll();
}
