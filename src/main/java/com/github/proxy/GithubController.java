package com.github.proxy;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
class GithubController {

    private final GithubService service;

    GithubController(GithubService service) {
        this.service = service;
    }

    @GetMapping("/{username}/repositories")
    List<Map<String, Object>> repositories(@PathVariable String username) {
        try {
            return service.getNonForkRepositories(username);
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User not found");
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    Map<String, Object> handle(UserNotFoundException e) {
        return Map.of(
                "status", 404,
                "message", e.getMessage()
        );
    }
}
