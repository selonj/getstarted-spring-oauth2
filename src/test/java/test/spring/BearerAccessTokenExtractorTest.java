package test.spring;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Administrator on 2016-04-12.
 */
public class BearerAccessTokenExtractorTest {
  final private BearerTokenExtractor tokenExtractor = new BearerTokenExtractor();
  final private MockHttpServletRequest request = new MockHttpServletRequest();
  private static final String ACCESS_TOKEN = "tonr";

  @Test
  public void retrievesAccessTokenFromRequestAuthorizationHeader() throws Exception {
    request.addHeader("Authorization", bearer(ACCESS_TOKEN));

    assertExtractedAccessToken(equalTo(ACCESS_TOKEN));
  }

  @Test
  public void retrievesAccessTokenFromRequestAccessTokenParameter() throws Exception {
    request.setParameter("access_token", ACCESS_TOKEN);

    assertExtractedAccessToken(equalTo(ACCESS_TOKEN));
  }

  private void assertExtractedAccessToken(Matcher<String> tokenMatcher) {
    Authentication authentication = tokenExtractor.extract(request);

    assertThat(authentication, principal(tokenMatcher));
  }

  private String bearer(String accessToken) {
    return "Bearer " + accessToken;
  }

  private Matcher<? super Authentication> principal(Matcher<String> principalMatcher) {
    return new FeatureMatcher<Authentication, String>(principalMatcher, "authentication principal", "but") {
      @Override protected String featureValueOf(Authentication authentication) {
        return (String) authentication.getPrincipal();
      }
    };
  }
}
