package holyflame.administration.controller;

import holyflame.administration.model.EnseignantAutorisation;
import holyflame.administration.repository.EnseignantAutorisationRepository;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parametres/autorisations")
public class AutorisationController {

    @Autowired private EnseignantAutorisationRepository autorisationRepository;
    @Autowired private EtablissementService etablissementService;

    @PostMapping("/ajouter")
    public String ajouter(
            @RequestParam Long enseignantId,
            @RequestParam Long matiereId,
            @RequestParam Long classeId,
            RedirectAttributes ra) {

        Long etabId = etablissementService.getCurrentEtablissementId();
        if (!autorisationRepository.existsByEnseignantIdAndMatiereIdAndClasseId(enseignantId, matiereId, classeId)) {
            EnseignantAutorisation a = new EnseignantAutorisation();
            a.setEnseignantId(enseignantId);
            a.setMatiereId(matiereId);
            a.setClasseId(classeId);
            a.setEtablissementId(etabId);
            autorisationRepository.save(a);
            ra.addFlashAttribute("autorisationMsg", "Autorisation ajoutée.");
        } else {
            ra.addFlashAttribute("autorisationMsg", "Cette autorisation existe déjà.");
        }
        return "redirect:/parametres?saved=true&tab=autorisations";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        autorisationRepository.deleteById(id);
        ra.addFlashAttribute("autorisationMsg", "Autorisation retirée.");
        return "redirect:/parametres?tab=autorisations";
    }
}
