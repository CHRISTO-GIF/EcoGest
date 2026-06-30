package holyflame.administration.controller;

import holyflame.administration.model.Matiere;
import holyflame.administration.repository.CreneauHoraireRepository;
import holyflame.administration.repository.MatiereRepository;
import holyflame.administration.repository.NoteRepository;
import holyflame.administration.service.EtablissementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/matieres")
public class MatiereController {

    @Autowired private MatiereRepository matiereRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private CreneauHoraireRepository creneauHoraireRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String index(Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        model.addAttribute("matieres", matiereRepository.findByEtablissementIdOrderByNomAsc(etabId));
        return "matieres";
    }

    @PostMapping
    public String ajouterMatiere(
            @RequestParam String nom,
            @RequestParam(required = false) Double coefficient,
            @RequestParam(required = false) String description) {

        Matiere matiere = new Matiere();
        matiere.setNom(nom);
        matiere.setCoefficient(coefficient != null ? coefficient : 1.0);
        matiere.setDescription(description);
        matiere.setEtablissementId(etablissementService.getCurrentEtablissementId());
        matiereRepository.save(matiere);
        return "redirect:/matieres";
    }

    @PostMapping("/{id}/modifier")
    public String modifierMatiere(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam(required = false) Double coefficient,
            @RequestParam(required = false) String description) {

        Matiere matiere = matiereRepository.findById(id).orElseThrow();
        matiere.setNom(nom);
        matiere.setCoefficient(coefficient);
        matiere.setDescription(description);
        if (matiere.getEtablissementId() == null) {
            matiere.setEtablissementId(etablissementService.getCurrentEtablissementId());
        }
        matiereRepository.save(matiere);
        return "redirect:/matieres";
    }

    @Transactional
    @PostMapping("/{id}/supprimer")
    public String supprimerMatiere(@PathVariable Long id, RedirectAttributes ra) {
        long nbNotes = noteRepository.countByMatiereId(id);
        if (nbNotes > 0) {
            String nom = matiereRepository.findById(id).map(Matiere::getNom).orElse("cette matière");
            ra.addFlashAttribute("erreur",
                "Impossible de supprimer \"" + nom + "\" : " + nbNotes
                + " note(s) y sont associées. Supprimez-les d'abord depuis la saisie de notes.");
            return "redirect:/matieres";
        }
        creneauHoraireRepository.deleteByMatiereId(id);
        matiereRepository.deleteById(id);
        ra.addFlashAttribute("success", "Matière supprimée avec succès.");
        return "redirect:/matieres";
    }
}
