package Open.Source.Project.Portal;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

/**
 * Unit test for GitHubService verifying graceful error handling and fallback metrics.
 */
class GitHubServiceTest {

    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        gitHubService = new GitHubService(builder);
        gitHubService.setOwner("kishoreavk7");
        gitHubService.setRepository("open-source-project-portal");
    }

    @Test
    @DisplayName("Fallback metrics should contain stargazers_count, forks_count, and open_issues_count")
    void testFallbackMetricsContainRequiredKeys() {
        Map<String, Object> fallback = gitHubService.createFallbackMetrics();

        assertNotNull(fallback);
        assertEquals(0, fallback.get("stargazers_count"));
        assertEquals(0, fallback.get("forks_count"));
        assertEquals(0, fallback.get("open_issues_count"));
        assertEquals("open-source-project-portal", fallback.get("name"));
        assertEquals("FALLBACK_MODE", fallback.get("api_status"));
    }

    @Test
    @DisplayName("getRepositoryDetails should fail gracefully and not throw exception on invalid host/failure")
    void testGracefulFallbackWhenExternalCallFails() {
        // Point to an invalid unreachable URL to simulate network outage / GitHub API downtime
        gitHubService.setOwner("invalid-nonexistent-owner-abc-xyz-12345");
        gitHubService.setRepository("invalid-repo-test");

        Map<String, Object> result = gitHubService.getRepositoryDetails();

        assertNotNull(result, "Service should return fallback metrics instead of null or throwing exception");
        assertNotNull(result.get("stargazers_count"));
        assertNotNull(result.get("forks_count"));
        assertNotNull(result.get("open_issues_count"));
    }
}
