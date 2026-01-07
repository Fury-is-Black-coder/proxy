package com.github.proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class GithubProxyIntegrationTest {

    static WireMockServer wireMock = new WireMockServer(wireMockConfig().port(8089));

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("github.api.url", () -> "http://localhost:8089");
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
    void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        reset();
    }

    @Test
    void shouldReturnNonForkRepositoriesWithBranches() throws Exception {
        // GitHub: repos
        stubFor(get("/users/testuser/repos")
                .willReturn(okJson("""
                    [
                      { "name": "repo-1", "fork": false, "owner": { "login": "testuser" } },
                      { "name": "repo-2", "fork": true,  "owner": { "login": "testuser" } }
                    ]
                """)));

        // GitHub: branches
        stubFor(get("/repos/testuser/repo-1/branches")
                .willReturn(okJson("""
                    [
                      { "name": "main", "commit": { "sha": "abc123" } }
                    ]
                """)));

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/users/testuser/repositories")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].repositoryName").value("repo-1"))
                .andExpect(jsonPath("$[0].ownerLogin").value("testuser"))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("abc123"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        stubFor(get("/users/missing/repos").willReturn(notFound()));

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .get("/users/missing/repositories")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }
}
