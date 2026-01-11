package com.github.proxy;

public record BranchResponse(
        String name,
        String lastCommitSha
) {
}