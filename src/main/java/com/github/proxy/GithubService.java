package com.github.proxy;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
class GithubService {

    private final GithubClient githubClient;

    GithubService(final GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    List<RepositoryResponse> getUserRepositories(final String username) {
        final List<GithubRepository> repositories;

        try {
            repositories = githubClient.getRepositories(username);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new UserNotFoundException(username);
        }

        return repositories.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> mapToRepositoryResponse(username, repo))
                .toList();
    }

    private RepositoryResponse mapToRepositoryResponse(
            final String username,
            final GithubRepository repository
    ) {
        final var branches = githubClient.getBranches(username, repository.name())
                .stream()
                .map(branch -> new BranchResponse(
                        branch.name(),
                        branch.commit().sha()
                ))
                .toList();

        return new RepositoryResponse(
                repository.name(),
                repository.owner().login(),
                branches
        );
    }
}
