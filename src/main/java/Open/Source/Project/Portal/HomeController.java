package Open.Source.Project.Portal;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller handling the home page view for the Open Source Project Portal.
 */
@Controller
public class HomeController {

    private final GitHubService gitHubService;

    public HomeController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Map<String, Object> repository = gitHubService.getRepositoryDetails();

        Object stars = repository.getOrDefault("stargazers_count", 0);
        Object forks = repository.getOrDefault("forks_count", 0);
        Object issues = repository.getOrDefault("open_issues_count", 0);
        Object repoName = repository.getOrDefault("name", "open-source-project-portal");
        Object htmlUrl = repository.getOrDefault("html_url", "https://github.com/kishoreavk7/open-source-project-portal");
        Object description = repository.getOrDefault("description", "Open Source Project Portal DevOps Project");
        Object apiStatus = repository.getOrDefault("api_status", "LIVE");

        model.addAttribute("stars", stars);
        model.addAttribute("forks", forks);
        model.addAttribute("issues", issues);
        model.addAttribute("repoName", repoName);
        model.addAttribute("repoUrl", htmlUrl);
        model.addAttribute("repoDescription", description);
        model.addAttribute("apiStatus", apiStatus);
        model.addAttribute("appStatus", "UP");

        return "index";
    }
}