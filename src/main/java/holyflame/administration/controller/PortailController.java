package holyflame.administration.controller;

import holyflame.administration.model.Eleve;
import holyflame.administration.model.Note;
import holyflame.administration.repository.*;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/portail")
public class PortailController {

    @Autowired private EleveRepository eleveRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private AbsenceRepository absenceRepository;
    @Autowired private PaiementRepository paiementRepository;
    @Autowired private PublicationRepository publicationRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String portail(@RequestParam(defaultValue = "1") Integer trimestre,
                          Authentication auth, Model model) {

        String email = auth != null ? auth.getName() : "";

        Optional<Eleve> eleveOpt = eleveRepository.findByCompteEmail(email);
        if (eleveOpt.isEmpty()) eleveOpt = eleveRepository.findByEmailParent(email);

        if (eleveOpt.isEmpty()) {
            model.addAttribute("pasDeCompte", true);
            return "portail";
        }

        // Migration automatique si etablissementId est null
        Eleve eleveRaw = eleveOpt.get();
        if (eleveRaw.getEtablissementId() == null) {
            Long etabIdAdmin = etablissementService.getCurrentEtablissementId();
            if (etabIdAdmin != null) eleveRepository.migrateNullEtablissementId(etabIdAdmin);
        }
        // Recharger depuis la base pour avoir l'etabId à jour
        final Eleve eleve = eleveRepository.findById(eleveRaw.getId()).orElse(eleveRaw);

        List<Note> notes = noteRepository.findByEleveAndTrimestreOrderByMatiereNomAsc(eleve, trimestre);

        double somme = notes.stream()
            .filter(n -> n.getValeur() != null && n.getCoefficient() != null)
            .mapToDouble(n -> n.getValeur() * n.getCoefficient()).sum();
        double totalCoef = notes.stream()
            .filter(n -> n.getCoefficient() != null)
            .mapToDouble(Note::getCoefficient).sum();
        double moyenne = totalCoef > 0 ? Math.round((somme / totalCoef) * 100.0) / 100.0 : 0;

        Long etabId = eleve.getEtablissementId();
        var publications = (etabId != null && eleve.getClasse() != null)
            ? publicationRepository.findForEleve(etabId, eleve.getClasse().getId())
            : Collections.emptyList();

        model.addAttribute("eleve",           eleve);
        model.addAttribute("notes",           notes);
        model.addAttribute("trimestre",       trimestre);
        model.addAttribute("moyenneGenerale", moyenne);
        model.addAttribute("publications",    publications);
        model.addAttribute("absences",        absenceRepository.findAll().stream()
            .filter(a -> eleve.equals(a.getEleve()))
            .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
            .limit(20).toList());
        model.addAttribute("paiements",       paiementRepository.findAll().stream()
            .filter(p -> eleve.equals(p.getEleve()))
            .sorted((a, b) -> b.getDatePaiement().compareTo(a.getDatePaiement()))
            .limit(20).toList());
        return "portail";
    }
}
