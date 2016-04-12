package com.selonj.getstarted.oauth2.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import static java.util.Arrays.asList;

/**
 * Created by Administrator on 2016-04-12.
 */
@Import({OAuth2ServerConfig.ResourceServerConfig.class,
    OAuth2ServerConfig.AuthorizationServerConfig.class})
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

    @Override public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.inMemory().withClient(CLIENT_ID).secret(CLIENT_SECRET).
          authorizedGrantTypes("client_credentials", "refresh_token").
          scopes("read").authorities("ROLE_USER").
          resourceIds(OAUTH2_RESOURCE);
    }

    @Override public void configure(AuthorizationServerEndpointsConfigurer endpoints)
        throws Exception {
      endpoints.tokenStore(tokenStore).authenticationManager(authenticationManager).tokenGranter(clientCredentialsTokenGranterAllowsRefreshToken(endpoints)).
          allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    public TokenGranter clientCredentialsTokenGranterAllowsRefreshToken(AuthorizationServerEndpointsConfigurer endpoints) {
      return new CompositeTokenGranter(asList(clientCredentialsTokenGranter(endpoints), refreshTokenGranter(endpoints)));
    }

    private TokenGranter refreshTokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
      return new RefreshTokenGranter(
          endpoints.getTokenServices(),
          endpoints.getClientDetailsService(),
          endpoints.getOAuth2RequestFactory()
      );
    }

    private TokenGranter clientCredentialsTokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
      ClientCredentialsTokenGranter tokenGranter = new ClientCredentialsTokenGranter(
          endpoints.getTokenServices(),
          endpoints.getClientDetailsService(),
          endpoints.getOAuth2RequestFactory()
      );
      tokenGranter.setAllowRefresh(true);
      return tokenGranter;
    }

    @Bean
    public TokenStore memoryTokenStore() {
      return new InMemoryTokenStore();
    }

    @Override public void configure(AuthorizationServerSecurityConfigurer security)
        throws Exception {
      security.allowFormAuthenticationForClients();
    }
  }
}
