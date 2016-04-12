package com.selonj.getstarted.oauth2.examples;

import com.selonj.getstarted.oauth2.config.OAuth2ServerConfig;
import com.selonj.getstarted.oauth2.supports.OAuth2AccessToken;
import com.selonj.getstarted.oauth2.supports.ClientDriver;
import com.selonj.getstarted.oauth2.supports.OAuth2Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Administrator on 2016-04-12.
 */
public class GetStartedOAuth2Example {
  private static final OAuth2AccessToken NO_ACCESS_TOKEN = null;
  private OAuth2Server server = new OAuth2Server();
  private ClientDriver client = new ClientDriver(OAuth2ServerConfig.CLIENT_ID, OAuth2ServerConfig.CLIENT_SECRET, server);

  @Before
  public void startServer() throws Exception {
    server.start();
  }

  @After
  public void stopServer() throws Exception {
    server.stop();
  }

  @Test
  public void notifiesClientUnauthorizedWhenFetchingResourceWithoutAccessToken() throws Exception {
    client.fetchResource(NO_ACCESS_TOKEN);

    client.hasNotifiedUnauthorized();
  }

  @Test
  public void clientObtainsResourceWhenFetchingResourcesWithinAnAuthorizedAccessToken() throws Exception {
    OAuth2AccessToken accessToken = client.fetchAccessToken();

    client.fetchResource(accessToken);

    client.hasResponseBodyReceived("bearer tonr");
  }

  @Test
  public void notifiesClientUnauthorizedWhenFetchingResourceInvalidAccessToken() throws Exception {
    client.fetchResource(invalidAccessToken());

    client.hasNotifiedAccessTokenInvalid();
  }

  @Test
  public void clientRefreshAccessTokenByRefreshToken() throws Exception {
    OAuth2AccessToken accessToken = client.fetchAccessToken();

    OAuth2AccessToken refreshed = client.refreshAccessToken(accessToken);

    assertThat(refreshed.accessToken(), not(equalTo(accessToken.accessToken())));
    assertThat(refreshed.refreshToken(), equalTo(accessToken.refreshToken()));
  }

  private OAuth2AccessToken invalidAccessToken() {
    return OAuth2AccessToken.from("{\"access_token\":\"<invalid token>\"}");
  }
}
