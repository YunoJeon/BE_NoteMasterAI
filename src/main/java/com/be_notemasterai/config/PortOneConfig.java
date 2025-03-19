package com.be_notemasterai.config;

import com.siot.IamportRestClient.IamportClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "portone.api")
@Getter
@Setter
public class PortOneConfig {

  private String key;
  private String secret;

  @Bean
  public IamportClient iamportClient() {
    return new IamportClient(key, secret);
  }
}