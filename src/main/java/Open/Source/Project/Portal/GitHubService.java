package Open.Source.Project.Portal;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GitHubService {

    private final RestClient restClient;

    @Value("${github.owner}")
    private String owner;

    @Value("${github.repository}")
    private String repository;

    public GitHubService(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public Map<String, Object> getRepositoryDetails() {

        String url = "https://api.github.com/repos/"
                + owner + "/" + repository;

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(Map.class);
    }
}
