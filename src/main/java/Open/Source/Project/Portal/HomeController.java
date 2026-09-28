
package Open.Source.Project.Portal;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final GitHubService gitHubService;

    public HomeController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/")
    public String home(Model model) {

        Map<String, Object> repository =
                gitHubService.getRepositoryDetails();

        model.addAttribute(
                "stars",
                repository.get("stargazers_count")
        );

        model.addAttribute(
                "forks",
                repository.get("forks_count")
        );

        model.addAttribute(
                "issues",
                repository.get("open_issues_count")
        );

        return "index";
    }
}