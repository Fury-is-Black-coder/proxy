package com.github.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class GithubClient {

    private final RestClient restClient;

    GithubClient(
            final RestClient.Builder builder,
            @Value("${github.api.url:https://api.github.com}") final String baseUrl
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    List<GithubRepository> getRepositories(final String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }

    List<GithubBranch> getBranches(final String username, final String repositoryName) {
        return restClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repositoryName)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }
}
