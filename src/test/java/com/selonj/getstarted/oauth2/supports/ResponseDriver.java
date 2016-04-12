package com.selonj.getstarted.oauth2.supports;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by Administrator on 2016-04-12.
 */
public class ResponseDriver {
  public final String responseBody;
  private final HttpResponse response;

  public ResponseDriver(HttpResponse response) throws IOException {
    this.response = response;
    this.responseBody = readResponseBody(response);
    reportsErrorWhen(serverCannotWorking());
    reportsErrorWhen(resourceNotFound());
  }

  public static Matcher<Integer> isOk() {
    return is(HttpStatus.SC_OK);
  }

  public static Matcher<Integer> resourceNotFound() {
    return equalTo(SC_NOT_FOUND);
  }

  private static Matcher<Integer> serverCannotWorking() {
    return new TypeSafeDiagnosingMatcher<Integer>() {
      @Override
      protected boolean matchesSafely(Integer status, Description description) {
        if (SC_INTERNAL_SERVER_ERROR <= status) {
          return true;
        }
        description.appendText("server work normally!");
        return false;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("server can't work");
      }
    };
  }

  public static Matcher<Integer> isUnauthorized() {
    return is(SC_UNAUTHORIZED);
  }

  public void reportsErrorWhen(Matcher<Integer> statusMatcher) throws IOException {
    assertResponseStatus(not(statusMatcher));
  }

  public void assertResponseStatus(Matcher<Integer> statusMatcher) throws IOException {
    assertThat(responseBody, response.getStatusLine().getStatusCode(), statusMatcher);
  }

  private String readResponseBody(HttpResponse response) throws IOException {
    InputStream in = response.getEntity().getContent();
    try {
      return IOUtils.toString(in);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public void assertResponseBody(Matcher<String> bodyMatcher) {
    assertThat(responseBody, bodyMatcher);
  }
}
