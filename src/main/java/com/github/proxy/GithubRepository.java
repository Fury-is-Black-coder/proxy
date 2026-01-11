package com.github.proxy;

public record GithubRepository(
        String name,
        boolean fork,
        GithubOwner owner
) {
}
