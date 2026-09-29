package Open.Source.Project.Portal;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HealthController providing a lightweight JSON health endpoint.
 * Complements Spring Boot Actuator at /actuator/health.
 */
@RestController
public class HealthController {

    @Value("${spring.application.name:open-source-project-portal}")
    private String applicationName;

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("timestamp", Instant.now().toString());
        health.put("checks", Map.of(
            "appServer", "UP",
            "jvm", "UP"
        ));
        return ResponseEntity.ok(health);
    }
}
