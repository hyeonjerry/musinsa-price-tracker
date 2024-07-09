package com.tonggn.msspt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MssptApplication {

  public static void main(final String[] args) {
    SpringApplication.run(MssptApplication.class, args);
  }

}
