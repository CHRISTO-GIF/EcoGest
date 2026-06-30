package holyflame.administration.controller;

import holyflame.administration.model.CreneauHoraire;
import holyflame.administration.repository.*;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/emploi-du-temps")
public class EmploiDuTempsController {

    @Autowired private CreneauHoraireRepository creneauRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private MatiereRepository matiereRepository;
    @Autowired private ParametreRepository parametreRepository;
    @Autowired private EtablissementService etablissementService;

    private static final String[] JOURS = {"", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};

    @GetMapping
    public String index(@RequestParam(required = false) Long classeId, Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();

        List<CreneauHoraire> creneaux = classeId != null
            ? creneauRepository.findByEtablissementIdAndClasseIdOrderByJourAscHeureDebutAsc(etabId, classeId)
            : creneauRepository.findByEtablissementIdOrderByJourAscHeureDebutAsc(etabId);

        // Organiser par jour : Map<Integer jour, List<Creneau>>
        Map<Integer, List<CreneauHoraire>> parJour = new LinkedHashMap<>();
        for (int j = 1; j <= 6; j++) {
            final int jour = j;
            List<CreneauHoraire> liste = creneaux.stream()
                .filter(c -> jour == c.getJour()).collect(Collectors.toList());
            parJour.put(j, liste);
        }

        String nomEtab = parametreRepository
            .findByCleAndEtablissementId("NOM_ETABLISSEMENT", etabId)
            .map(p -> p.getValeur()).orElse("HolyFlame");
        String annee = parametreRepository
            .findByCleAndEtablissementId("ANNEE_SCOLAIRE", etabId)
            .map(p -> p.getValeur()).orElse("2025-2026");

        List<holyflame.administration.model.Classe> classes = classeRepository.findAll().stream()
            .filter(c -> etabId == null || etabId.equals(c.getEtablissementId())).toList();

        String classeNom = classeId != null
            ? classes.stream().filter(c -> c.getId().equals(classeId))
                .findFirst().map(c -> c.getNom()).orElse("Classe inconnue")
            : "Toutes les classes";

        model.addAttribute("parJour",   parJour);
        model.addAttribute("jours",     JOURS);
        model.addAttribute("classes",   classes);
        model.addAttribute("matieres",  matiereRepository.findAll().stream()
            .filter(m -> etabId == null || etabId.equals(m.getEtablissementId())).toList());
        model.addAttribute("classeId",  classeId);
        model.addAttribute("classeNom", classeNom);
        model.addAttribute("nomEtab",   nomEtab);
        model.addAttribute("annee",     annee);
        return "emploi-du-temps";
    }

    @PostMapping
    public String ajouter(
            @RequestParam Integer jour,
            @RequestParam String heureDebut,
            @RequestParam String heureFin,
            @RequestParam Long classeId,
            @RequestParam Long matiereId,
            @RequestParam(required = false) String enseignantNom,
            @RequestParam(required = false) String salle,
            RedirectAttributes ra) {

        Long etabId = etablissementService.getCurrentEtablissementId();
        CreneauHoraire c = new CreneauHoraire();
        c.setJour(jour);
        c.setHeureDebut(heureDebut);
        c.setHeureFin(heureFin);
        c.setClasse(classeRepository.findById(classeId).orElseThrow());
        c.setMatiere(matiereRepository.findById(matiereId).orElseThrow());
        c.setEnseignantNom(enseignantNom);
        c.setSalle(salle);
        c.setEtablissementId(etabId);
        creneauRepository.save(c);
        ra.addFlashAttribute("success", "Créneau ajouté.");
        return "redirect:/emploi-du-temps?classeId=" + classeId;
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam Integer jour,
            @RequestParam String heureDebut,
            @RequestParam String heureFin,
            @RequestParam Long classeId,
            @RequestParam Long matiereId,
            @RequestParam(required = false) String enseignantNom,
            @RequestParam(required = false) String salle,
            RedirectAttributes ra) {

        creneauRepository.findById(id).ifPresent(c -> {
            c.setJour(jour);
            c.setHeureDebut(heureDebut);
            c.setHeureFin(heureFin);
            c.setClasse(classeRepository.findById(classeId).orElse(c.getClasse()));
            c.setMatiere(matiereRepository.findById(matiereId).orElse(c.getMatiere()));
            c.setEnseignantNom(enseignantNom);
            c.setSalle(salle);
            creneauRepository.save(c);
        });
        ra.addFlashAttribute("success", "Créneau modifié avec succès.");
        return "redirect:/emploi-du-temps" + (classeId != 0 ? "?classeId=" + classeId : "");
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id,
                            @RequestParam(required = false) Long classeId,
                            RedirectAttributes ra) {
        creneauRepository.deleteById(id);
        ra.addFlashAttribute("success", "Créneau supprimé.");
        return "redirect:/emploi-du-temps" + (classeId != null ? "?classeId=" + classeId : "");
    }
}
