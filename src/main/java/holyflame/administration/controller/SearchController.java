package holyflame.administration.controller;

import holyflame.administration.repository.*;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired private EleveRepository eleveRepository;
    @Autowired private PersonnelRepository personnelRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private MatiereRepository matiereRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping("/recherche")
    public String recherche(@RequestParam(defaultValue = "") String q, Model model) {
        String term = q.trim();
        model.addAttribute("q", term);

        if (term.length() < 2) {
            model.addAttribute("tropCourt", true);
            return "recherche";
        }

        Long etabId = etablissementService.getCurrentEtablissementId();

        if (etabId != null) {
            model.addAttribute("eleves",    eleveRepository.searchByEtablissement(term, etabId));
            model.addAttribute("personnels", personnelRepository.searchByEtablissement(term, etabId));
        } else {
            model.addAttribute("eleves",    java.util.List.of());
            model.addAttribute("personnels", java.util.List.of());
        }

        // Classes et matières : filtre simple sur nom
        String low = term.toLowerCase();
        model.addAttribute("classes", classeRepository.findAll().stream()
            .filter(c -> etabId == null || etabId.equals(c.getEtablissementId()))
            .filter(c -> c.getNom() != null && c.getNom().toLowerCase().contains(low))
            .toList());
        model.addAttribute("matieres", matiereRepository.findAll().stream()
            .filter(m -> etabId == null || etabId.equals(m.getEtablissementId()))
            .filter(m -> m.getNom() != null && m.getNom().toLowerCase().contains(low))
            .toList());

        return "recherche";
    }
}
