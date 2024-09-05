package com.tonggn.msspt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("pages/index");
    registry.addViewController("/products/search").setViewName("pages/search_result");
    registry.addViewController("/products/price-drop").setViewName("pages/price_drop");
    registry.addViewController("/products/{id}").setViewName("pages/product_detail");
  }
}
