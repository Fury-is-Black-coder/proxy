# GitHub Proxy API

A simple REST application built with **Java 25** and **Spring Boot 4**, acting as a proxy for the **GitHub REST API v3**.  
The application exposes an endpoint that allows fetching a GitHub user's repositories (excluding forks) together with branch information and the latest commit SHA.

---

## Running the application

### Requirements
- Java 25
- Gradle (wrapper included in the project)

### Start the application
```bash
./gradlew bootRun
```

The application starts by default at: http://localhost:8080

### Endpoint API
Fetch user repositories
GET /users/{username}/repositories
http://localhost:8080/users/octocat/repositories

Response – 200 OK
**Description**  
Returns a list of repositories that are not forks, including:
- `repositoryName` – repository name
- `ownerLogin` – repository owner login
- `branches`
    - `name` – branch name
    - `lastCommitSha` – last commit SHA

**Example response**
```json
[
  {
    "repositoryName": "example-repo",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "abc123"
      }
    ]
  }
]
```


Response – 404 Not Found
http://localhost:8080/users/non-existing-user/repositories
```json
{
  "status": 404,
  "message": "User not found"
}
```

### Tests
- Integration tests are implemented
- WireMock is used to emulate the GitHub API (no real external calls)
- Tested scenarios:
  - successful retrieval of repositories and branches
  - handling of a non-existing user (404)

Run tests using:
```bash
./gradlew test
```

### Design decisions
- Architecture: Controller / Service / Client
- All classes are located in a single package (as required)
- No separate DTO/domain layers — simple immutable records are used as proxy models
- No WebFlux, security, caching, or pagination
- Minimal number of classes and tests

### Backing API
The application uses:
- GitHub REST API v3
- The application uses:: https://developer.github.com/v3