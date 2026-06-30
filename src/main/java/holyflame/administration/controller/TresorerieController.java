package holyflame.administration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tresorerie")
public class TresorerieController {

    @GetMapping
    public String index() {
        return "redirect:/finances";
    }
}
