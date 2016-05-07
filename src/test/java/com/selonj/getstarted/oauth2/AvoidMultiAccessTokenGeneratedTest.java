package com.selonj.getstarted.oauth2;

import com.selonj.getstarted.oauth2.supports.ClientDriver;
import com.selonj.getstarted.oauth2.supports.OAuth2AccessToken;
import com.selonj.getstarted.oauth2.supports.OAuth2Server;
import com.selonj.getstarted.oauth2.supports.TaskExecutor;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.selonj.getstarted.oauth2.config.OAuth2ServerConfig.CLIENT_ID;
import static com.selonj.getstarted.oauth2.config.OAuth2ServerConfig.CLIENT_SECRET;
import static com.selonj.getstarted.oauth2.config.WebSecurityConfig.PASSWORD;
import static com.selonj.getstarted.oauth2.config.WebSecurityConfig.USERNAME;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Administrator on 2016-04-12.
 */
public class AvoidMultiAccessTokenGeneratedTest {
  private final TaskExecutor tasks = new TaskExecutor();
  private OAuth2Server server = new OAuth2Server();

  private ClientDriver client() {
    return new ClientDriver(CLIENT_ID, CLIENT_SECRET, USERNAME, PASSWORD, server);
  }

  @Before
  public void startServer() throws Exception {
    server.start();
  }

  @After
  public void stopServer() throws Exception {
    server.stop();
  }

  @Test public void avoidsMultiAccessTokenGeneratedByAddingAuthenticationIdWithUniqueConstraints() throws Exception {
    tasks.spawns(new Runnable() {
      @Override public void run() {
        try {
          client().fetchAccessToken();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
      }
    }, 20).waitUntilTimeout(5000);
  }
}
