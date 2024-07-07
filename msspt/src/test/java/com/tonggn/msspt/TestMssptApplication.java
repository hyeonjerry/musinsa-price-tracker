package com.tonggn.msspt;

import org.springframework.boot.SpringApplication;

public class TestMssptApplication {

  public static void main(String[] args) {
    SpringApplication.from(MssptApplication::main).with(TestcontainersConfiguration.class)
        .run(args);
  }

}
