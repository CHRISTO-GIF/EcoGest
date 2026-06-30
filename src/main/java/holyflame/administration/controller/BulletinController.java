package holyflame.administration.controller;

import holyflame.administration.model.Eleve;
import holyflame.administration.model.Note;
import holyflame.administration.repository.ClasseRepository;
import holyflame.administration.repository.EleveRepository;
import holyflame.administration.repository.NoteRepository;
import holyflame.administration.repository.ParametreRepository;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bulletins")
public class BulletinController {

    @Autowired private EleveRepository eleveRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private ParametreRepository parametreRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String liste(@RequestParam(required = false) Long classeId, Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        List<Eleve> eleves = classeId != null
            ? eleveRepository.findByClasseIdOrderByNomAsc(classeId)
            : eleveRepository.findByEtablissementIdOrderByNomAscPrenomAsc(etabId);

        model.addAttribute("eleves",  eleves);
        model.addAttribute("classes", classeRepository.findByEtablissementId(etabId));
        model.addAttribute("classeId", classeId);
        return "bulletins";
    }

    // ── Impression en lot : tous les bulletins d'une classe pour un trimestre ──
    @GetMapping("/impression")
    public String impressionClasse(@RequestParam(required = false) Long classeId,
                                   @RequestParam(defaultValue = "1") Integer trimestre,
                                   Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();

        List<Eleve> eleves = classeId != null
            ? eleveRepository.findByClasseIdOrderByNomAsc(classeId)
            : eleveRepository.findByEtablissementIdOrderByNomAscPrenomAsc(etabId);

        // Construire la liste des bulletins : un map par élève
        List<Map<String, Object>> bulletins = new ArrayList<>();
        for (Eleve eleve : eleves) {
            List<Note> notes = noteRepository.findByEleveAndTrimestreOrderByMatiereNomAsc(eleve, trimestre);
            double somme = notes.stream()
                .filter(n -> n.getValeur() != null && n.getCoefficient() != null)
                .mapToDouble(n -> n.getValeur() * n.getCoefficient()).sum();
            double totalCoef = notes.stream()
                .filter(n -> n.getCoefficient() != null)
                .mapToDouble(Note::getCoefficient).sum();
            double moyenne = totalCoef > 0 ? Math.round((somme / totalCoef) * 100.0) / 100.0 : 0;

            Map<String, Object> b = new LinkedHashMap<>();
            b.put("eleve",      eleve);
            b.put("notes",      notes);
            b.put("moyenne",    moyenne);
            b.put("mention",    getMention(moyenne));
            b.put("mentionCss", getMentionCss(moyenne));
            b.put("appreciation", getAppreciation(moyenne));
            bulletins.add(b);
        }

        // Calcul du rang de chaque élève dans sa classe
        bulletins.sort((a, b) -> Double.compare((Double) b.get("moyenne"), (Double) a.get("moyenne")));
        for (int i = 0; i < bulletins.size(); i++) {
            bulletins.get(i).put("rang", i + 1);
            bulletins.get(i).put("effectif", bulletins.size());
        }

        String nomEtab = parametreRepository
            .findByCleAndEtablissementId("NOM_ETABLISSEMENT", etabId)
            .map(p -> p.getValeur()).orElse("HolyFlame");
        String classeNom = classeId != null
            ? classeRepository.findById(classeId).map(c -> c.getNom()).orElse("Toutes les classes")
            : "Toutes les classes";

        model.addAttribute("bulletins",  bulletins);
        model.addAttribute("trimestre",  trimestre);
        model.addAttribute("classeNom",  classeNom);
        model.addAttribute("nomEtab",    nomEtab);
        model.addAttribute("classeId",   classeId);
        model.addAttribute("classes",    classeRepository.findByEtablissementId(etabId));
        return "bulletins-impression";
    }

    private String getMention(double moyenne) {
        if (moyenne >= 16) return "TRÈS BIEN";
        if (moyenne >= 14) return "BIEN";
        if (moyenne >= 12) return "ASSEZ BIEN";
        if (moyenne >= 10) return "PASSABLE";
        return "INSUFFISANT";
    }

    private String getMentionCss(double moyenne) {
        if (moyenne >= 16) return "bg-success";
        if (moyenne >= 14) return "bg-primary";
        if (moyenne >= 12) return "bg-info text-dark";
        if (moyenne >= 10) return "bg-warning text-dark";
        return "bg-danger";
    }

    private String getAppreciation(double moyenne) {
        if (moyenne >= 18) return "Résultats exceptionnels. Félicitations du conseil de classe.";
        if (moyenne >= 16) return "Excellents résultats. Toutes nos félicitations.";
        if (moyenne >= 14) return "Très bons résultats. Encouragements du conseil de classe.";
        if (moyenne >= 12) return "Bons résultats. Peut encore progresser.";
        if (moyenne >= 10) return "Résultats satisfaisants. Des efforts restent nécessaires.";
        if (moyenne >= 8)  return "Résultats insuffisants. Un travail sérieux s'impose.";
        return "Résultats très insuffisants. Une remise en question est nécessaire.";
    }

    @GetMapping("/{eleveId}")
    public String bulletin(@PathVariable Long eleveId,
                           @RequestParam(defaultValue = "1") Integer trimestre,
                           Model model,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        Eleve eleve = eleveRepository.findById(eleveId).orElse(null);
        if (eleve == null || (etabId != null && !etabId.equals(eleve.getEtablissementId()))) {
            ra.addFlashAttribute("erreur", "Élève introuvable.");
            return "redirect:/bulletins";
        }
        List<Note> notes = noteRepository.findByEleveAndTrimestreOrderByMatiereNomAsc(eleve, trimestre);

        double somme = notes.stream()
            .filter(n -> n.getValeur() != null && n.getCoefficient() != null)
            .mapToDouble(n -> n.getValeur() * n.getCoefficient()).sum();
        double totalCoef = notes.stream()
            .filter(n -> n.getCoefficient() != null)
            .mapToDouble(Note::getCoefficient).sum();
        double moyenne = totalCoef > 0 ? somme / totalCoef : 0;

        double moyenneArrondie = Math.round(moyenne * 100.0) / 100.0;

        // Rang dans la classe
        int rang = 1; int effectif = 1;
        if (eleve.getClasse() != null) {
            List<Eleve> camarades = eleveRepository.findByClasseIdOrderByNomAsc(eleve.getClasse().getId());
            effectif = camarades.size();
            for (Eleve cam : camarades) {
                if (cam.getId().equals(eleve.getId())) continue;
                List<Note> notesCam = noteRepository.findByEleveAndTrimestreOrderByMatiereNomAsc(cam, trimestre);
                double sommeCam = notesCam.stream().filter(n -> n.getValeur() != null && n.getCoefficient() != null)
                    .mapToDouble(n -> n.getValeur() * n.getCoefficient()).sum();
                double coefCam = notesCam.stream().filter(n -> n.getCoefficient() != null).mapToDouble(Note::getCoefficient).sum();
                double moyenneCam = coefCam > 0 ? sommeCam / coefCam : 0;
                if (moyenneCam > moyenne) rang++;
            }
        }

        model.addAttribute("eleve", eleve);
        model.addAttribute("notes", notes);
        model.addAttribute("trimestre", trimestre);
        model.addAttribute("moyenneGenerale", moyenneArrondie);
        model.addAttribute("mention", getMention(moyenneArrondie));
        model.addAttribute("mentionCss", getMentionCss(moyenneArrondie));
        model.addAttribute("appreciation", getAppreciation(moyenneArrondie));
        model.addAttribute("rang", rang);
        model.addAttribute("effectif", effectif);
        return "bulletin";
    }
}
