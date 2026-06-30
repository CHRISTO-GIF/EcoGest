package holyflame.administration.controller;

import holyflame.administration.repository.JournalActionRepository;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/journal")
public class JournalController {

    @Autowired private JournalActionRepository journalRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String journal(@RequestParam(required = false) String module,
                          @RequestParam(defaultValue = "100") int limit,
                          Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        PageRequest pr = PageRequest.of(0, Math.min(limit, 500));

        var actions = (module != null && !module.isBlank())
            ? journalRepository.findByEtablissementIdAndModuleOrderByDateDesc(etabId, module, pr)
            : journalRepository.findByEtablissementIdOrderByDateDesc(etabId, pr);

        model.addAttribute("actions", actions);
        model.addAttribute("module", module);
        model.addAttribute("modules", java.util.List.of(
            "ELEVES", "FINANCES", "NOTES", "ABSENCES", "RH", "PARAMETRES", "COMMUNICATION", "BULLETINS"
        ));
        return "journal";
    }
}
