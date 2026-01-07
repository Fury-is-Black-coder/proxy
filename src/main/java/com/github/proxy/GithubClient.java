package com.github.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
class GithubClient {

    private final RestClient restClient;

    GithubClient(@Value("${github.api.url:https://api.github.com}") String baseUrl) {
        this.restClient = RestClient.create(baseUrl);
    }


    List<Map<String, Object>> getRepositories(String user) {
        return restClient.get()
                .uri("/users/{user}/repos", user)
                .retrieve()
                .body(List.class);
    }

    List<Map<String, Object>> getBranches(String user, String repo) {
        return restClient.get()
                .uri("/repos/{user}/{repo}/branches", user, repo)
                .retrieve()
                .body(List.class);
    }
}
