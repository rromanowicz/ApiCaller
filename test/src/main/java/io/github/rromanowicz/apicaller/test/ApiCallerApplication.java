package io.github.rromanowicz.apicaller.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"io.github.rromanowicz.apicaller"})
public class ApiCallerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiCallerApplication.class, args);
  }

}
