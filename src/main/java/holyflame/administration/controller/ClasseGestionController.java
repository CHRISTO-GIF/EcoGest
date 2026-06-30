package holyflame.administration.controller;

import holyflame.administration.model.Classe;
import holyflame.administration.repository.ClasseRepository;
import holyflame.administration.repository.CreneauHoraireRepository;
import holyflame.administration.repository.EleveRepository;
import holyflame.administration.repository.ProgrammeRepository;
import holyflame.administration.service.EtablissementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestion-classes")
public class ClasseGestionController {

    @Autowired private ClasseRepository classeRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private ProgrammeRepository programmeRepository;
    @Autowired private CreneauHoraireRepository creneauHoraireRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String index(Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        model.addAttribute("classes", classeRepository.findByEtablissementId(etabId));
        return "gestion-classes";
    }

    @PostMapping
    public String ajouterClasse(
            @RequestParam String nom,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String anneeScolaire) {

        Classe classe = new Classe();
        classe.setNom(nom);
        classe.setNiveau(niveau);
        classe.setAnneeScolaire(anneeScolaire != null ? anneeScolaire : "2025-2026");
        classe.setEtablissementId(etablissementService.getCurrentEtablissementId());
        classeRepository.save(classe);
        return "redirect:/gestion-classes";
    }

    @PostMapping("/{id}/modifier")
    public String modifierClasse(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String anneeScolaire) {

        Classe classe = classeRepository.findById(id).orElseThrow();
        classe.setNom(nom);
        classe.setNiveau(niveau);
        classe.setAnneeScolaire(anneeScolaire);
        classeRepository.save(classe);
        return "redirect:/gestion-classes";
    }

    @Transactional
    @PostMapping("/{id}/supprimer")
    public String supprimerClasse(@PathVariable Long id, RedirectAttributes ra) {
        long nbEleves = eleveRepository.countByClasseId(id);
        if (nbEleves > 0) {
            ra.addFlashAttribute("erreur",
                "Impossible de supprimer cette classe : elle contient " + nbEleves
                + " élève(s). Réaffectez ou supprimez-les d'abord depuis le Secrétariat.");
            return "redirect:/gestion-classes";
        }
        programmeRepository.deleteByClasseId(id);
        creneauHoraireRepository.deleteByClasseId(id);
        classeRepository.deleteById(id);
        ra.addFlashAttribute("success", "Classe supprimée avec succès.");
        return "redirect:/gestion-classes";
    }
}
