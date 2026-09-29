package Open.Source.Project.Portal;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Unit/Integration test for HomeController using MockMvc.
 * Mocks GitHubService so tests do not depend on the external network or GitHub API.
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubService gitHubService;

    @Test
    @DisplayName("GET / should return 200 OK and populate model with GitHub metrics")
    void testHomePageSuccess() throws Exception {
        Map<String, Object> mockDetails = new HashMap<>();
        mockDetails.put("stargazers_count", 42);
        mockDetails.put("forks_count", 15);
        mockDetails.put("open_issues_count", 3);
        mockDetails.put("name", "open-source-project-portal");
        mockDetails.put("html_url", "https://github.com/kishoreavk7/open-source-project-portal");
        mockDetails.put("description", "Open Source Project Portal DevOps Project");
        mockDetails.put("api_status", "MOCKED_TEST");

        when(gitHubService.getRepositoryDetails()).thenReturn(mockDetails);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("stars", 42))
                .andExpect(model().attribute("forks", 15))
                .andExpect(model().attribute("issues", 3))
                .andExpect(model().attribute("repoName", "open-source-project-portal"))
                .andExpect(model().attribute("apiStatus", "MOCKED_TEST"));
    }
}
