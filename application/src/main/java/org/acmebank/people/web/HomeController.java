package org.acmebank.people.web;

import org.acmebank.people.domain.port.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Principal principal, Model model) {
        if (principal != null) {
            userRepository.findByEmail(principal.getName()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                boolean isManager = !userRepository.findByManagerId(user.id()).isEmpty();
                model.addAttribute("isManager", isManager);
                model.addAttribute("isIta", user.isIta());
            });
        }
        return "index";
    }
}
