package com.be_notemasterai.config;

import com.be_notemasterai.security.resolver.CurrentMemberArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final CurrentMemberArgumentResolver currentMemberArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

    resolvers.add(currentMemberArgumentResolver);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry.addMapping("/api/**")
        .allowCredentials(true)
        .allowedOrigins("http://localhost:5173")
        .allowedHeaders("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
  }
}