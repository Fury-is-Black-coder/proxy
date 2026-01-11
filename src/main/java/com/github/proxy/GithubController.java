package com.github.proxy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GithubController {

    private final GithubService githubService;

    GithubController(final GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repositories")
    List<RepositoryResponse> getRepositories(
            @PathVariable final String username
    ) {
        return githubService.getUserRepositories(username);
    }
}
