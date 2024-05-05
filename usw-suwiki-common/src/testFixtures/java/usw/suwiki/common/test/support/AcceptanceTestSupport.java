package usw.suwiki.common.test.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import usw.suwiki.common.test.HttpMethod;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class AcceptanceTestSupport {
  protected final String INVALID_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                                                ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ" +
                                                ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

  @Autowired
  private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
      .alwaysDo(print())
      .apply(documentationConfiguration(restDocumentation))
      .addFilter(new CharacterEncodingFilter(UTF_8.displayName(), true))
      .defaultRequest(RestDocumentationRequestBuilders.post("/**").with(csrf().asHeader()))
      .defaultRequest(RestDocumentationRequestBuilders.get("/**").with(csrf().asHeader()))
      .defaultRequest(RestDocumentationRequestBuilders.put("/**").with(csrf().asHeader()))
      .defaultRequest(RestDocumentationRequestBuilders.patch("/**").with(csrf().asHeader()))
      .defaultRequest(RestDocumentationRequestBuilders.delete("/**").with(csrf().asHeader()))
      .build();
  }

  public ResultActions get(Uri uri, String accessToken) throws Exception {
    return perform(uri, accessToken, null);
  }

  public ResultActions get(Uri uri, Pair... parameters) throws Exception {
    return perform(uri, null, parameters);
  }

  public ResultActions get(Uri uri, String accessToken, Pair... parameters) throws Exception {
    return perform(uri, accessToken, parameters);
  }

  public ResultActions getNonJson(Uri uri, Pair... parameters) throws Exception {
    return performNonJson(uri, null, parameters);
  }

  public ResultActions post(Uri uri, Object requestBody) throws Exception {
    return perform(uri, HttpMethod.POST, null, requestBody);
  }

  public ResultActions post(Uri uri, String accessToken, Object requestBody) throws Exception {
    return perform(uri, HttpMethod.POST, accessToken, requestBody);
  }

  public ResultActions post(Uri uri, String accessToken, Object requestBody, Pair... parameters) throws Exception {
    return perform(uri, HttpMethod.POST, accessToken, requestBody, parameters);
  }

  public ResultActions put(Uri uri, Object requestBody) throws Exception {
    return perform(uri, HttpMethod.PUT, null, requestBody);
  }

  public ResultActions put(Uri uri, String accessToken, Object requestBody, Pair... parameters) throws Exception {
    return perform(uri, HttpMethod.PUT, accessToken, requestBody, parameters);
  }

  public ResultActions put(Uri uri, String accessToken, Object requestBody) throws Exception {
    return perform(uri, HttpMethod.PUT, accessToken, requestBody);
  }

  public ResultActions patch(Uri uri, Object requestBody) throws Exception {
    return perform(uri, HttpMethod.PATCH, null, requestBody);
  }

  public ResultActions patch(Uri uri, String accessToken, Object requestBody) throws Exception {
    return perform(uri, HttpMethod.PATCH, accessToken, requestBody);
  }

  public ResultActions delete(Uri uri, String accessToken) throws Exception {
    return perform(uri, HttpMethod.DELETE, accessToken, null);
  }

  public ResultActions delete(Uri uri, String accessToken, Object requestBody) throws Exception {
    return perform(uri, HttpMethod.DELETE, accessToken, requestBody);
  }

  public ResultActions delete(Uri uri, String accessToken, Object requestBody, Pair... parameters) throws Exception {
    return perform(uri, HttpMethod.DELETE, accessToken, requestBody, parameters);
  }

  /**
   * query 전용 acceptance test template
   */
  private ResultActions perform(Uri uri, String accessToken, Pair... parameters) throws Exception {
    var request = toRequestBuilder(uri, HttpMethod.GET);

    if (parameters != null) {
      request.queryParams(toParams(parameters));
    }

    return perform(request, accessToken);
  }

  private ResultActions performNonJson(Uri uri, String accessToken, Pair... parameters) throws Exception {
    var request = toRequestBuilderNonJsonRequest(uri, HttpMethod.GET);

    if (parameters != null) {
      request.queryParams(toParams(parameters));
    }

    return perform(request, accessToken);
  }

  private MultiValueMap<String, String> toParams(Pair... parameters) {
    return Arrays.stream(parameters).collect(
      LinkedMultiValueMap::new,
      (map, pair) -> map.add(pair.first(), pair.second()),
      LinkedMultiValueMap::addAll
    );
  }

  /**
   * command 전용 acceptance test template
   */
  private ResultActions perform(Uri uri, HttpMethod httpMethod, String accessToken, Object requestBody) throws Exception {
    var request = toRequestBuilder(uri, httpMethod);

    if (requestBody != null) {
      request.content(objectMapper.writeValueAsString(requestBody));
    }

    return perform(request, accessToken);
  }

  private ResultActions perform(Uri uri, HttpMethod httpMethod, String accessToken, Object requestBody, Pair... parameters) throws Exception {
    var request = toRequestBuilder(uri, httpMethod);

    if (requestBody != null) {
      request.content(objectMapper.writeValueAsString(requestBody));
    }

    if (parameters != null) {
      request.queryParams(toParams(parameters));
    }

    return perform(request, accessToken);
  }

  /**
   * 결과적으로 요청을 호출하는 method
   */
  private ResultActions perform(MockHttpServletRequestBuilder request, String accessToken) throws Exception {
    if (accessToken != null) {
      request.header(AUTHORIZATION, accessToken);
    }

    return mockMvc.perform(request);
  }

  private MockHttpServletRequestBuilder toRequestBuilder(Uri uri, HttpMethod httpMethod) {
    var request = switch (httpMethod) {
      case GET -> RestDocumentationRequestBuilders.get(uri.resource);
      case POST -> RestDocumentationRequestBuilders.post(uri.resource);
      case PUT -> RestDocumentationRequestBuilders.put(uri.resource);
      case PATCH -> RestDocumentationRequestBuilders.patch(uri.resource);
      case DELETE -> RestDocumentationRequestBuilders.delete(uri.resource);
    };

    return request.accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .requestAttr("org.springframework.restdocs.urlTemplate", uri.urlTemplate); // rest-docs pathvariable 설정
  }

  private MockHttpServletRequestBuilder toRequestBuilderNonJsonRequest(Uri uri, HttpMethod httpMethod) {
    var request = switch (httpMethod) {
      case GET -> RestDocumentationRequestBuilders.get(uri.resource);
      case POST -> RestDocumentationRequestBuilders.post(uri.resource);
      case PUT -> RestDocumentationRequestBuilders.put(uri.resource);
      case PATCH -> RestDocumentationRequestBuilders.patch(uri.resource);
      case DELETE -> RestDocumentationRequestBuilders.delete(uri.resource);
    };

    return request.accept(MediaType.TEXT_HTML_VALUE)
      .contentType(MediaType.TEXT_HTML_VALUE)
      .requestAttr("org.springframework.restdocs.urlTemplate", uri.urlTemplate); // rest-docs pathvariable 설정
  }
}
