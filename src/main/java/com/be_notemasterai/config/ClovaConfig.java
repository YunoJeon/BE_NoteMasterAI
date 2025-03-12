package com.be_notemasterai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "naver.cloud.clova.stt")
@Getter
@Setter
public class ClovaConfig {

  private String secret;
}
