package Open.Source.Project.Portal;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service to interact with the GitHub REST API and fetch repository metrics.
 * Built with Spring RestClient and includes graceful error handling.
 */
@Service
public class GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

    private final RestClient.Builder restClientBuilder;

    @Value("${github.owner:kishoreavk7}")
    private String owner;

    @Value("${github.repository:open-source-project-portal}")
    private String repository;

    @Value("${github.token:}")
    private String token;

    public GitHubService(RestClient.Builder builder) {
        this.restClientBuilder = builder;
    }

    /**
     * Fetches repository statistics dynamically from GitHub API.
     * Fails gracefully if the GitHub API is offline, rate-limited, or unreachable.
     *
     * @return Map containing repository details and metrics
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRepositoryDetails() {
        String url = "https://api.github.com/repos/" + owner + "/" + repository;
        logger.info("Fetching repository metrics dynamically from: {}", url);

        try {
            RestClient.Builder builder = restClientBuilder
                    .defaultHeader("User-Agent", "Open-Source-Project-Portal-App")
                    .defaultHeader("Accept", "application/vnd.github.v3+json");

            // Attach optional authorization header if GITHUB_TOKEN is provided
            if (token != null && !token.trim().isEmpty()) {
                builder.defaultHeader("Authorization", "Bearer " + token.trim());
            }

            RestClient restClient = builder.build();

            Map<String, Object> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Map.class);

            if (response != null && !response.isEmpty()) {
                logger.info("Successfully fetched GitHub metrics. Stars: {}, Forks: {}, Issues: {}",
                        response.get("stargazers_count"),
                        response.get("forks_count"),
                        response.get("open_issues_count"));
                response.put("api_status", "LIVE");
                return response;
            }
        } catch (Exception ex) {
            logger.warn("GitHub API call encountered an issue ({}). Failing gracefully to fallback metrics.", ex.getMessage());
        }

        // Graceful fallback when GitHub API is unreachable, offline, or rate-limited
        return createFallbackMetrics();
    }

    /**
     * Fallback metrics returned when GitHub API is unavailable.
     */
    public Map<String, Object> createFallbackMetrics() {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("stargazers_count", 0);
        fallback.put("forks_count", 0);
        fallback.put("open_issues_count", 0);
        fallback.put("name", repository != null ? repository : "open-source-project-portal");
        fallback.put("description", "Open Source Project Portal DevOps Project");
        fallback.put("html_url", "https://github.com/" + (owner != null ? owner : "kishoreavk7") + "/" + (repository != null ? repository : "open-source-project-portal"));
        fallback.put("api_status", "FALLBACK_MODE");
        return fallback;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
