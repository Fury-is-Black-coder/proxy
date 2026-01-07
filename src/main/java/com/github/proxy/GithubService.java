package com.github.proxy;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
class GithubService {

    private final GithubClient client;

    GithubService(GithubClient client) {
        this.client = client;
    }

    List<Map<String, Object>> getNonForkRepositories(String user) {
        List<Map<String, Object>> repos = client.getRepositories(user);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> repo : repos) {
            if (Boolean.FALSE.equals(repo.get("fork"))) {
                String repoName = (String) repo.get("name");
                List<Map<String, Object>> branches = client.getBranches(user, repoName);

                result.add(Map.of(
                        "repositoryName", repoName,
                        "ownerLogin", ((Map<?, ?>) repo.get("owner")).get("login"),
                        "branches", branches.stream().map(b -> Map.of(
                                "name", b.get("name"),
                                "lastCommitSha", ((Map<?, ?>) b.get("commit")).get("sha")
                        )).toList()
                ));
            }
        }
        return result;
    }
}
