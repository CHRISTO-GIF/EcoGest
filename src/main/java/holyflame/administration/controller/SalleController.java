package holyflame.administration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gestion-salles")
public class SalleController {

    @GetMapping
    public String index() {
        return "gestion-salles";
    }
}
