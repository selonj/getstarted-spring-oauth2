package com.selonj.getstarted.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/**
 * Created by Administrator on 2016-04-12.
 */
@EnableWebMvcSecurity
@Import(OAuth2ServerConfig.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  public static final String ROLE_USER = "USER";
  public static final String USERNAME = "mocha";
  public static final String PASSWORD = "foo";

  @Override public void configure(WebSecurity web) throws Exception {
    web.debug(true);
  }

  @Bean(name = "authenticationManagerBean")
  @Override public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser(USERNAME).password(PASSWORD).roles(ROLE_USER);
  }

  @Override protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().hasRole(ROLE_USER);
  }
}
