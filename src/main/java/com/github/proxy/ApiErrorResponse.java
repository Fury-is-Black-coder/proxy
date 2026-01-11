package com.github.proxy;

public record ApiErrorResponse(
        int status,
        String message
) {
}
