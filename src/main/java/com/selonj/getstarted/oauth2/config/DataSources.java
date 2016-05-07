package com.selonj.getstarted.oauth2.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javax.sql.DataSource;

/**
 * Created by L.x on 16-5-7.
 */
public class DataSources {
  public static DataSource defaults() {
    return mysql();
  }

  public static DataSource mysql() {
    MysqlDataSource mysql = new MysqlDataSource();
    mysql.setDatabaseName("test");
    mysql.setUser("root");
    mysql.setPassword("root");
    return mysql;
  }
}
