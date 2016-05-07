package com.selonj.getstarted.oauth2.supports;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import static com.selonj.getstarted.oauth2.config.DataSources.defaults;

/**
 * Created by Administrator on 2016-04-12.
 */
public class OAuth2Server {
  private static final int SERVER_PORT = 8899;
  private Server server = new Server(SERVER_PORT);
  private Database database = Database.in(defaults());

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
    database.execute(script("schema.sql"));
    server.start();
  }

  private String script(String resource) throws IOException {
    return IOUtils.toString(ClassLoader.getSystemResourceAsStream(resource), "utf-8");
  }

  public void stop() throws Exception {
    database.close();
    server.stop();
  }
}
