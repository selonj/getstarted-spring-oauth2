package com.selonj.getstarted.oauth2.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by Administrator on 2016-04-12.
 */
@Import({OAuth2ServerConfig.ResourceServerConfig.class,
    OAuth2ServerConfig.AuthorizationServerConfig.class})
@EnableTransactionManagement
public class OAuth2ServerConfig {
  private static final String OAUTH2_RESOURCE = "oauth2";
  public static final String CLIENT_ID = "tonr";
  public static final String CLIENT_SECRET = "1234";

  @EnableResourceServer
  public static class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override public void configure(HttpSecurity http) throws Exception {
      http.requestMatchers().antMatchers("/resource/**").and().
          authorizeRequests().anyRequest().access("#oauth2.hasScope('read')").and().
          sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
      resources.resourceId(OAUTH2_RESOURCE).stateless(true);
    }
  }

  @EnableAuthorizationServer
  public static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private UserDetailsService userDetailService;

    @Override public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.inMemory().withClient(CLIENT_ID).secret(CLIENT_SECRET).
          authorizedGrantTypes("password", "refresh_token").
          scopes("read").authorities("ROLE_USER").
          resourceIds(OAUTH2_RESOURCE);
    }

    @Override public void configure(AuthorizationServerEndpointsConfigurer endpoints)
        throws Exception {
      endpoints.tokenStore(tokenStore).userDetailsService(userDetailService).authenticationManager(authenticationManager).
          allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    private DataSource dataSource = DataSources.defaults();

    @Bean
    public TokenStore jdbcTokenStore() {
      return new JdbcTokenStore(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
      return new DataSourceTransactionManager(dataSource);
    }

    @Override public void configure(AuthorizationServerSecurityConfigurer security)
        throws Exception {
      security.allowFormAuthenticationForClients();
    }
  }
}
