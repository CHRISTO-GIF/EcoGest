package holyflame.administration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tableau-eleve")
public class EleveController {

    @GetMapping
    public String index() {
        return "redirect:/portail";
    }
}
