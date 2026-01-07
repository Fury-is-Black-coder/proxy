# GitHub Proxy API

Prosta aplikacja REST w **Java 25 + Spring Boot 4**, działająca jako proxy do **GitHub REST API v3**.  
Aplikacja udostępnia endpoint pozwalający pobrać repozytoria użytkownika GitHuba (z wyłączeniem forków) wraz z informacjami o branchach i ostatnich commitach.

---

## Uruchomienie aplikacji

### Wymagania
- Java 25
- Gradle (wrapper w projekcie)

### Start aplikacji
```bash
./gradlew bootRun
```

Aplikacja uruchamia się domyślnie na: http://localhost:8080

### Endpoint API
Pobranie repozytoriów użytkownika
GET /users/{username}/repositories
http://localhost:8080/users/octocat/repositories

Odpowiedź 200 OK
**Opis**  
Lista repozytoriów (bez forków) zawierająca:
- `repositoryName` – nazwa repozytorium
- `ownerLogin` – login właściciela
- `branches`
    - `name` – nazwa brancha
    - `lastCommitSha` – SHA ostatniego commita

**Przykładowa odpowiedź**
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


Odpowiedź – 404 Not Found
http://localhost:8080/users/uzytkownik-nieistnieje/repositories
```json
{
  "status": 404,
  "message": "User not found"
}
```

### Testy
- Zaimplementowano wyłącznie testy integracyjne
- Brak mocków — użyto WireMock do emulacji GitHub API
- Testowane scenariusze:
- poprawne pobranie repozytoriów i branchy
- obsługa nieistniejącego użytkownika (404)
Uruchomienie testów:
```bash
./gradlew test
```

### Decyzje projektowe
- Architektura: Controller / Service / Client
- Wszystkie klasy w jednym pakiecie (zgodnie z wymaganiami)
- Brak DTO — aplikacja działa jako proxy
- Brak WebFlux, security, cache, paginacji
- Minimalna liczba klas i testów

### Backing API
Aplikacja korzysta z:
- GitHub REST API v3
- Dokumentacja: https://developer.github.com/v3