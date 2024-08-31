package com.tonggn.msspt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("index");
    registry.addViewController("/products/search").setViewName("search");
    registry.addViewController("/products/price-drop").setViewName("price_drop");
    registry.addViewController("/products/{id}").setViewName("product_detail");
  }
}
