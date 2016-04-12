package com.selonj.getstarted.oauth2.supports;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016-04-12.
 */
public class OAuth2AccessToken {

  private static final String FIELD_PATTERN = "\"(.*?)\":\"(.*?)\"";
  private Map<String, String> fields = new HashMap<>();

  public static OAuth2AccessToken from(String json) {
    OAuth2AccessToken token = new OAuth2AccessToken();

    Matcher matcher = Pattern.compile(FIELD_PATTERN).matcher(json);

    while (matcher.find()) token.addField(nameIn(matcher), valueIn(matcher));

    return token;
  }

  private static String nameIn(Matcher matcher) {
    return matcher.group(1);
  }

  private static String valueIn(Matcher matcher) {
    return matcher.group(2);
  }

  private void addField(String name, String value) {
    fields.put(name, value);
  }

  public String accessToken() {
    return get("access_token");
  }

  public String refreshToken() {
    return get("refresh_token");
  }

  private String get(String name) {
    if (fields.containsKey(name)) return fields.get(name);
    throw new IllegalStateException(fields + " has not field: `" + name + "`");
  }
}
