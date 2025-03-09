package com.be_notemasterai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BeNoteMasterAiApplication {

  public static void main(String[] args) {
    SpringApplication.run(BeNoteMasterAiApplication.class, args);
  }

}
