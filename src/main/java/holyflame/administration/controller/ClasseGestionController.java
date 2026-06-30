package holyflame.administration.controller;

import holyflame.administration.model.Classe;
import holyflame.administration.model.Parametre;
import holyflame.administration.repository.ClasseRepository;
import holyflame.administration.repository.CreneauHoraireRepository;
import holyflame.administration.repository.EleveRepository;
import holyflame.administration.repository.ParametreRepository;
import holyflame.administration.repository.ProgrammeRepository;
import holyflame.administration.service.EtablissementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gestion-classes")
public class ClasseGestionController {

    @Autowired private ClasseRepository classeRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private ProgrammeRepository programmeRepository;
    @Autowired private CreneauHoraireRepository creneauHoraireRepository;
    @Autowired private ParametreRepository parametreRepository;
    @Autowired private EtablissementService etablissementService;

    private static final Map<String, List<String>> TYPES_NIVEAUX = new LinkedHashMap<>();
    private static final Map<String, String> TYPES_LABELS = new LinkedHashMap<>();
    static {
        TYPES_NIVEAUX.put("TYPE_MATERNELLE",    List.of("Petite Section", "Moyenne Section", "Grande Section"));
        TYPES_NIVEAUX.put("TYPE_PRIMAIRE",      List.of("CP1", "CP2", "CE1", "CE2", "CM1", "CM2"));
        TYPES_NIVEAUX.put("TYPE_COLLEGE",       List.of("6ème", "5ème", "4ème", "3ème"));
        TYPES_NIVEAUX.put("TYPE_LYCEE",         List.of("2nde", "1ère", "Terminale"));
        TYPES_NIVEAUX.put("TYPE_LYCEE_GENERAL", List.of("2nde G", "1ère G", "Terminale G"));

        TYPES_LABELS.put("TYPE_MATERNELLE",    "Maternelle");
        TYPES_LABELS.put("TYPE_PRIMAIRE",      "Primaire");
        TYPES_LABELS.put("TYPE_COLLEGE",       "Collège");
        TYPES_LABELS.put("TYPE_LYCEE",         "Lycée");
        TYPES_LABELS.put("TYPE_LYCEE_GENERAL", "Lycée Général");
    }

    @GetMapping
    public String index(Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        Map<String, String> p = parametreRepository.findByEtablissementId(etabId).stream()
            .collect(Collectors.toMap(Parametre::getCle, Parametre::getValeur, (a, b) -> a));
        String annee = p.getOrDefault("ANNEE_SCOLAIRE", "2025-2026");

        List<String> typesActifs = TYPES_LABELS.entrySet().stream()
            .filter(e -> "true".equals(p.get(e.getKey())))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        model.addAttribute("classes", classeRepository.findByEtablissementId(etabId));
        model.addAttribute("anneeScolaire", annee);
        model.addAttribute("typesActifs", typesActifs);
        return "gestion-classes";
    }

    @PostMapping
    public String ajouterClasse(
            @RequestParam String nom,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String anneeScolaire) {

        Long etabId = etablissementService.getCurrentEtablissementId();
        String annee = anneeScolaire;
        if (annee == null || annee.isBlank()) {
            annee = parametreRepository.findByCleAndEtablissementId("ANNEE_SCOLAIRE", etabId)
                .map(Parametre::getValeur).orElse("2025-2026");
        }

        Classe classe = new Classe();
        classe.setNom(nom);
        classe.setNiveau(niveau);
        classe.setAnneeScolaire(annee);
        classe.setEtablissementId(etabId);
        classeRepository.save(classe);
        return "redirect:/gestion-classes";
    }

    @PostMapping("/generer")
    public String genererClasses(RedirectAttributes ra) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        String annee = parametreRepository.findByCleAndEtablissementId("ANNEE_SCOLAIRE", etabId)
            .map(Parametre::getValeur).orElse("2025-2026");

        int count = 0;
        for (Map.Entry<String, List<String>> e : TYPES_NIVEAUX.entrySet()) {
            boolean actif = "true".equals(
                parametreRepository.findByCleAndEtablissementId(e.getKey(), etabId)
                    .map(Parametre::getValeur).orElse("false"));
            if (actif) {
                for (String nom : e.getValue()) {
                    if (!classeRepository.existsByNomAndAnneeScolaireAndEtablissementId(nom, annee, etabId)) {
                        Classe c = new Classe();
                        c.setNom(nom); c.setNiveau(nom);
                        c.setAnneeScolaire(annee); c.setEtablissementId(etabId);
                        classeRepository.save(c);
                        count++;
                    }
                }
            }
        }
        ra.addFlashAttribute("classesGenerees", count);
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
