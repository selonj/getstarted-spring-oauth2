package com.selonj.getstarted.oauth2.config;

import com.selonj.getstarted.oauth2.controllers.ResourceController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Administrator on 2016-04-12.
 */
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {
  @Bean
  public ResourceController resource() {
    return new ResourceController();
  }
}
