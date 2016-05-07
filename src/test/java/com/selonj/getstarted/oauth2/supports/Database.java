package com.selonj.getstarted.oauth2.supports;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 * Created by L.x on 16-5-7.
 */
public class Database {
  private final Connection connection;

  public Database(DataSource dataSource) throws SQLException {
    connection = dataSource.getConnection();
  }

  public static Database in(DataSource dataSource) {
    try {
      return new Database(dataSource);
    } catch (SQLException ex) {
      throw new Error(ex);
    }
  }

  public void wipe() {
    wipe("oauth_access_token");
    wipe("oauth_refresh_token");
  }

  private void wipe(String table) {
    execute("delete from " + table);
  }

  public void execute(String script) {
    try {
      Statement statement = connection.createStatement();
      for (String each : script.split(";")) statement.addBatch(each);
      statement.executeBatch();
      statement.close();
    } catch (SQLException ex) {
      throw new Error(ex);
    }
  }

  public void close() {
    try {
      connection.close();
    } catch (SQLException ignored) {
    }
  }
}
