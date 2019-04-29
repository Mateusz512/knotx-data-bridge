/*
 * Copyright (C) 2019 Knot.x Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.databridge.http.action;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.knotx.fragments.handler.api.fragment.FragmentResult.SUCCESS_TRANSITION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.knotx.databridge.http.action.common.configuration.EndpointOptions;
import io.knotx.fragment.Fragment;
import io.knotx.fragments.handler.api.fragment.FragmentContext;
import io.knotx.server.api.context.ClientRequest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.MultiMap;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class HttpActionTest {

  private static final String VALID_REQUEST_PATH = "/valid-service";
  private static final String VALID_JSON_RESPONSE_BODY = "{ \"data\": \"service response\"}";
  private static final String VALID_JSON_ARRAY_RESPONSE_BODY = "[ \"first service response\", \" second service response\"]";
  private static final String VALID_EMPTY_RESPONSE_BODY = "";

  private static final Fragment FRAGMENT = new Fragment("type", new JsonObject(), "expectedBody");
  private static final String HTTP_ACTION = "httpAction";

  @Mock
  private ClientRequest clientRequest;

  private WireMockServer wireMockServer;

  @BeforeEach
  void setUp() {
    this.wireMockServer = new WireMockServer(options().dynamicPort());
    this.wireMockServer.start();
  }

  @Test
  @DisplayName("Expect success transition when endpoint returned success status code")
  void expectSuccessTransitionWhenSuccessResponse(VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    HttpAction tested = configure(vertx, VALID_REQUEST_PATH, VALID_JSON_RESPONSE_BODY);

    // when
    tested.apply(new FragmentContext(FRAGMENT, new ClientRequest()),
        testContext.succeeding(result -> {
          testContext
              .verify(() -> assertEquals(SUCCESS_TRANSITION, result.getTransition()));
          testContext.completeNow();
        }));

    //then
    assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  @DisplayName("Expect fragment payload appended with endpoint result when endpoint responded with success status code and JSON body")
  void appendPayloadWhenEndpointResponseWithJsonObject(VertxTestContext testContext, Vertx vertx)
      throws Throwable {
    // given
    HttpAction tested = configure(vertx, VALID_REQUEST_PATH, VALID_JSON_RESPONSE_BODY);

    // when
    tested.apply(new FragmentContext(FRAGMENT, new ClientRequest()),
        testContext.succeeding(result -> {
          testContext
              .verify(() -> {
                assertTrue(result.getFragment().getPayload().containsKey(HTTP_ACTION),
                    "Service response should be available under HTTP action alias key.");
                assertEquals(new JsonObject(VALID_JSON_RESPONSE_BODY),
                    result.getFragment().getPayload().getJsonObject(HTTP_ACTION));
              });
          testContext.completeNow();
        }));

    //then
    assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  @DisplayName("Expect fragment payload appended with endpoint result when endpoint responded with success status code and JSONArray body")
  void appendPayloadWhenEndpointResponseWithJsonArrayVertxTestContext(VertxTestContext testContext,
      Vertx vertx) throws Throwable {
    // given
    HttpAction tested = configure(vertx, VALID_REQUEST_PATH, VALID_JSON_ARRAY_RESPONSE_BODY);

    // when
    tested.apply(new FragmentContext(FRAGMENT, new ClientRequest()),
        testContext.succeeding(result -> {
          testContext
              .verify(() -> {
                assertTrue(result.getFragment().getPayload().containsKey(HTTP_ACTION),
                    "Service response should be available under HTTP action alias key.");
                assertEquals(new JsonArray(VALID_JSON_ARRAY_RESPONSE_BODY),
                    result.getFragment().getPayload().getJsonArray(HTTP_ACTION));
              });
          testContext.completeNow();
        }));

    //then
    assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  @DisplayName("Expect fragment's body not modified when endpoint responded with OK and empty body")
  void fragmentsBodyNotModifiedWhenEmptyResponseBody(VertxTestContext testContext,
      Vertx vertx) throws Throwable {
    // given
    HttpAction tested = configure(vertx, VALID_REQUEST_PATH, VALID_EMPTY_RESPONSE_BODY);

    // when
    tested.apply(new FragmentContext(FRAGMENT, new ClientRequest()),
        testContext.succeeding(result -> {
          testContext
              .verify(() -> assertEquals(FRAGMENT.getBody(), result.getFragment().getBody()));
          testContext.completeNow();
        }));

    //then
    assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @Test
  @DisplayName("Expect response metadata in payload when endpoint returned success status code")
  void responseMetadataInPayloadWhenSuccessResponse() {

  }

  @Test
  @DisplayName("Expect response metadata in payload when endpoint returned error status code")
  void responseMetadataInPayloadWhenErrorResponse() {

  }

  @Test
  @DisplayName("Expect error transition when endpoint returned error status code")
  void errorTransitionWhenErrorStatusCode() {

  }

  @Test
  @DisplayName("Expect error transition when endpoint returned not valid JSON")
  void errorTransitionWhenResponseIsNotJson() {

  }

  @Test
  @DisplayName("Expect error transition when endpoint times out")
  void errorTransitionWhenEndpointTimesOut() {

  }

  @Test
  @DisplayName("Expect error transition when calling not existing endpoint")
  void errorTransitionWhenEndpointDoesNotExist() {

  }

  @Test
  @DisplayName("Expect headers from FragmentContext clientRequest are filtered and sent in endpoint request")
  void headersFromClientRequestFilteredAndSendToEndpoint() {

  }

  @Test
  @DisplayName("Expect additionalHeaders from EndpointOptions are sent in endpoint request")
  void additionalHeadersSentToEndpoint() {

  }

  @Test
  @DisplayName("Expect additionalHeaders override headers from FragmentContext clientRequest")
  void additionalHeadersOverrideClientRequestHeaders() {
  }

  @Test
  @DisplayName("Expect endpoint called with placeholders in path resolved with values from headers from FragmentContext clientRequest")
  void placeholdersInPathResolvedWithHeadersValues() {

  }

  @Test
  @DisplayName("Expect endpoint called with placeholders in path resolved with values from FragmentContext clientRequest query params")
  void placehodersInPathResolvedWithClientRequestQueryParams() {

  }

  private HttpAction configure(Vertx vertx, String requestPath, String responseBody) {
    wireMockServer.stubFor(get(urlEqualTo(requestPath))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(responseBody)));
    when(clientRequest.getPath()).thenReturn(requestPath);
    when(clientRequest.getHeaders()).thenReturn(MultiMap.caseInsensitiveMultiMap());

    EndpointOptions endpointOptions = new EndpointOptions().setPath(requestPath)
        .setDomain("localhost").setPort(wireMockServer.port());

    return new HttpAction(vertx,
        new HttpActionOptions().setEndpointOptions(endpointOptions), HTTP_ACTION);
  }
}