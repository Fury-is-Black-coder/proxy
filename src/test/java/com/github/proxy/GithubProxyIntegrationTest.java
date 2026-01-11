package com.github.proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubProxyIntegrationTest {

    static WireMockServer wireMock =
            new WireMockServer(wireMockConfig().port(8089));

    @LocalServerPort
    int port;

    @Autowired
    RestClient.Builder restClientBuilder;

    RestClient restClient;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", () -> "http://localhost:8089");
    }

    @BeforeAll
    static void startWireMock() {
        wireMock.start();
        configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @BeforeEach
    void setupClient() {
        restClient = restClientBuilder
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldReturnNonForkRepositoriesWithBranches() throws Exception {
        stubFor(get("/users/testuser/repos")
                .willReturn(okJson("""
                    [
                      { "name": "repo-1", "fork": false, "owner": { "login": "testuser" } },
                      { "name": "repo-2", "fork": true,  "owner": { "login": "testuser" } }
                    ]
                """)));

        stubFor(get("/repos/testuser/repo-1/branches")
                .willReturn(okJson("""
                    [
                      { "name": "main", "commit": { "sha": "abc123" } }
                    ]
                """)));

        String response = restClient.get()
                .uri("/users/testuser/repositories")
                .retrieve()
                .body(String.class);

        JSONAssert.assertEquals("""
            [
              {
                "repositoryName": "repo-1",
                "ownerLogin": "testuser",
                "branches": [
                  {
                    "name": "main",
                    "lastCommitSha": "abc123"
                  }
                ]
              }
            ]
        """, response, true);
    }
}
