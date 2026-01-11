package com.github.proxy;

public record GithubBranch(
        String name,
        GithubCommit commit
) {
}
