package com.github.proxy;

class UserNotFoundException extends RuntimeException {

    UserNotFoundException(final String username) {
        super("User not found: " + username);
    }
}
