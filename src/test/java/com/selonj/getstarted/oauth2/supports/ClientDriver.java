package com.selonj.getstarted.oauth2.supports;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.Matcher;

import static com.selonj.getstarted.oauth2.supports.ResponseDriver.isOk;
import static com.selonj.getstarted.oauth2.supports.ResponseDriver.isUnauthorized;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by Administrator on 2016-04-12.
 */
public class ClientDriver {
  private HttpClient client = HttpClients.createMinimal();
  private final String clientId;
  private final String clientSecret;
  private final String username;
  private final String password;
  private final OAuth2Server server;
  private ResponseDriver driver;

  public ClientDriver(String clientId, String clientSecret, String username, String password, OAuth2Server server) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.username = username;
    this.password = password;
    this.server = server;
  }

  public OAuth2AccessToken fetchAccessToken() throws IOException {
    fetch(format("/oauth/token?client_id=%s&client_secret=%s&username=%s&password=%s&grant_type=password", clientId, clientSecret, username, password));

    driver.assertResponseStatus(isOk());

    return OAuth2AccessToken.from(driver.responseBody);
  }

  public OAuth2AccessToken refreshAccessToken(OAuth2AccessToken tokenToRefresh) throws IOException {
    fetch(format("/oauth/token?client_id=%s&client_secret=%s&grant_type=refresh_token&refresh_token=%s", clientId, clientSecret, tokenToRefresh.refreshToken()));

    driver.assertResponseStatus(isOk());

    return OAuth2AccessToken.from(driver.responseBody);
  }

  public void fetchResource(OAuth2AccessToken token) throws IOException {
    fetch(format("/resource/ping?%s", withAccessToken(token)));
  }

  private String withAccessToken(OAuth2AccessToken token) throws UnsupportedEncodingException {
    if (token == null) return "";
    return String.format("access_token=%s", URLEncoder.encode(token.accessToken(), "UTF-8"));
  }

  private void fetch(String path) throws IOException {
    driver = new ResponseDriver(client.execute(new HttpGet(server.theFullURL(path))));
  }

  public void hasNotifiedUnauthorized() throws IOException {
    assertUnauthorizedResponseBodyMatching(containsString("unauthorized"));
  }

  public void hasNotifiedAccessTokenInvalid() throws IOException {
    assertUnauthorizedResponseBodyMatching(containsString("invalid token"));
  }

  private void assertUnauthorizedResponseBodyMatching(Matcher<String> bodyMatcher) throws IOException {
    driver.assertResponseStatus(isUnauthorized());
    driver.assertResponseBody(bodyMatcher);
  }

  public void hasResponseBodyReceived(String expectedResponseBody) {
    driver.assertResponseBody(equalTo(expectedResponseBody));
  }
}
