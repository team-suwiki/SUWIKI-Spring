package usw.suwiki.comon.test.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import usw.suwiki.comon.test.RequestType;
import usw.suwiki.comon.test.Table;

import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
public abstract class AcceptanceTestSupport {

  public static final String ANY_END_POINT = "/**";

  @Autowired
  protected static ObjectMapper objectMapper;

  @Autowired
  protected DatabaseCleaner databaseCleaner;

  private MockMvc mockMvc;

  protected abstract Set<Table> targetTables();

  protected abstract void clean();

  @BeforeEach
  public void setUp(
      WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation
  ) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .alwaysDo(print())
        .apply(documentationConfiguration(restDocumentation))
        .addFilter(new CharacterEncodingFilter(UTF_8.displayName(), true))
        .defaultRequest(post(ANY_END_POINT).with(csrf().asHeader()))
        .defaultRequest(get(ANY_END_POINT).with(csrf().asHeader()))
        .defaultRequest(put(ANY_END_POINT).with(csrf().asHeader()))
        .defaultRequest(delete(ANY_END_POINT).with(csrf().asHeader()))
        .build();
  }

  public ResultActions perform(
      String endpoint,
      RequestType requestType,
      String accessToken,
      List<Pair<String, String>> parameters,
      Object requestBody
  ) throws Exception {
    MockHttpServletRequestBuilder request = toRequestBuilder(endpoint, requestType)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    if (accessToken != null) {
      request.header(AUTHORIZATION, accessToken);
    }

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    if (parameters != null) {
      for (Pair<String, String> parameter : parameters) {
        queryParams.add(parameter.getFirst(), parameter.getSecond());
      }
      request.params(queryParams);
    }

    if (requestBody != null) {
      request.content(objectMapper.writeValueAsString(requestBody));
    }

    return mockMvc.perform(request);
  }

  private static MockHttpServletRequestBuilder toRequestBuilder(
      String uri,
      RequestType requestType
  ) {
    return switch (requestType) {
      case GET -> get(uri);
      case POST -> post(uri);
      case PUT -> put(uri);
      case PATCH -> patch(uri);
      case DELETE -> delete(uri);
    };
  }
}
