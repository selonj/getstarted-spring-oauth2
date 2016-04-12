package com.selonj.getstarted.oauth2.supports;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Created by Administrator on 2016-04-12.
 */
public class OAuth2Server {
  private static final int SERVER_PORT = 8899;
  private Server server = new Server(SERVER_PORT);

  public OAuth2Server() {
    server.setHandler(runAsWebApplication());
    server.setStopAtShutdown(true);
  }

  private Handler runAsWebApplication() {
    WebAppContext webApp = new WebAppContext();
    webApp.setWar("src/main/webapp");
    return webApp;
  }

  public String theFullURL(String path) {
    return String.format("http://localhost:%d/%s", SERVER_PORT, path);
  }

  public void start() throws Exception {
    server.start();
  }

  public void stop() throws Exception {
    server.stop();
  }
}
