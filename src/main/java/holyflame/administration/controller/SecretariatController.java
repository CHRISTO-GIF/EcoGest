package holyflame.administration.controller;

import holyflame.administration.model.Absence;
import holyflame.administration.model.Classe;
import holyflame.administration.model.Eleve;
import holyflame.administration.model.Parametre;
import holyflame.administration.model.Utilisateur;
import holyflame.administration.repository.AbsenceRepository;
import holyflame.administration.repository.ClasseRepository;
import holyflame.administration.repository.EleveRepository;
import holyflame.administration.repository.NoteRepository;
import holyflame.administration.repository.PaiementRepository;
import holyflame.administration.repository.ParametreRepository;
import holyflame.administration.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import holyflame.administration.service.EtablissementService;
import holyflame.administration.service.FileStorageService;
import holyflame.administration.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/secretariat")
public class SecretariatController {

    @Autowired private EleveRepository eleveRepository;
    @Autowired private AbsenceRepository absenceRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private PaiementRepository paiementRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private ParametreRepository parametreRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EtablissementService etablissementService;
    @Autowired private JournalService journalService;
    @Autowired private FileStorageService fileStorageService;

    @GetMapping
    public String index(Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        model.addAttribute("eleves",        eleveRepository.findByEtablissementIdOrderByNomAscPrenomAsc(etabId));
        model.addAttribute("classes",       classeRepository.findByEtablissementId(etabId));
        var absences = absenceRepository.findByEtablissementId(etabId);
        model.addAttribute("absences",      absences);
        model.addAttribute("totalAbsences", absences.size());
        return "secretariat";
    }

    @PostMapping(value = "/eleves", consumes = "multipart/form-data")
    public String ajouterEleve(
            @RequestParam String matricule,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String telephoneParent,
            @RequestParam(required = false) String emailParent,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String statutInscription,
            @RequestParam Long classeId,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) MultipartFile[] documents,
            RedirectAttributes ra) {

        Classe classe = classeRepository.findById(classeId).orElseThrow();
        Eleve eleve = new Eleve();
        eleve.setMatricule(matricule);
        eleve.setNom(nom.toUpperCase());
        eleve.setPrenom(prenom);
        eleve.setDateNaissance(dateNaissance);
        eleve.setTelephoneParent(telephoneParent);
        eleve.setEmailParent(emailParent);
        eleve.setAdresse(adresse);
        eleve.setStatutInscription(statutInscription != null ? statutInscription : "INSCRIT");
        eleve.setClasse(classe);
        eleve.setEtablissementId(etablissementService.getCurrentEtablissementId());

        try {
            if (photo != null && !photo.isEmpty()) {
                eleve.setPhotoPath(fileStorageService.store(photo, "eleves/photos"));
            }
            if (documents != null) {
                List<String> chemins = new ArrayList<>();
                for (MultipartFile doc : documents) {
                    if (doc != null && !doc.isEmpty()) {
                        chemins.add(fileStorageService.store(doc, "eleves/documents"));
                    }
                }
                if (!chemins.isEmpty()) {
                    eleve.setDocumentsPaths(String.join(",", chemins));
                }
            }
        } catch (IOException e) {
            ra.addFlashAttribute("erreurMsg", "Erreur lors de l'envoi des fichiers.");
            return "redirect:/secretariat";
        }

        eleveRepository.save(eleve);
        journalService.log("ÉLÈVE_AJOUTÉ", "ELEVES", nom.toUpperCase() + " " + prenom + " — " + matricule);
        return "redirect:/secretariat";
    }

    @GetMapping("/eleves/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        model.addAttribute("eleve", eleveRepository.findById(id).orElseThrow());
        model.addAttribute("classes", classeRepository.findByEtablissementId(etabId));
        return "eleve-form";
    }

    @PostMapping("/eleves/{id}/modifier")
    public String modifierEleve(
            @PathVariable Long id,
            @RequestParam String matricule,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String telephoneParent,
            @RequestParam(required = false) String emailParent,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String statutInscription,
            @RequestParam Long classeId) {

        Eleve eleve = eleveRepository.findById(id).orElseThrow();
        Classe classe = classeRepository.findById(classeId).orElseThrow();
        eleve.setMatricule(matricule);
        eleve.setNom(nom.toUpperCase());
        eleve.setPrenom(prenom);
        eleve.setDateNaissance(dateNaissance);
        eleve.setTelephoneParent(telephoneParent);
        eleve.setEmailParent(emailParent);
        eleve.setAdresse(adresse);
        eleve.setStatutInscription(statutInscription);
        eleve.setClasse(classe);
        eleveRepository.save(eleve);
        journalService.log("ÉLÈVE_MODIFIÉ", "ELEVES", nom.toUpperCase() + " " + prenom + " — " + matricule);
        return "redirect:/secretariat";
    }

    @Transactional
    @PostMapping("/eleves/{id}/supprimer")
    public String supprimerEleve(@PathVariable Long id) {
        eleveRepository.findById(id).ifPresent(e ->
            journalService.log("ÉLÈVE_SUPPRIMÉ", "ELEVES", e.getNom() + " " + e.getPrenom()));
        noteRepository.deleteByEleveId(id);
        absenceRepository.deleteByEleveId(id);
        paiementRepository.deleteByEleveId(id);
        eleveRepository.deleteById(id);
        return "redirect:/secretariat";
    }

    // ── Créer un compte portail pour un élève ──────────────────────────────
    @PostMapping("/eleves/{id}/creer-compte")
    public String creerCompteEleve(@PathVariable Long id,
                                   @RequestParam String email,
                                   @RequestParam String motDePasse,
                                   RedirectAttributes ra) {
        Eleve eleve = eleveRepository.findById(id).orElse(null);
        if (eleve == null) {
            ra.addFlashAttribute("erreurMsg", "Élève introuvable.");
            return "redirect:/secretariat";
        }
        if (email == null || email.isBlank()) {
            ra.addFlashAttribute("erreurMsg", "L'adresse email est obligatoire.");
            return "redirect:/secretariat";
        }
        String emailTrim = email.trim().toLowerCase();
        if (utilisateurRepository.findByEmail(emailTrim).isPresent()) {
            ra.addFlashAttribute("erreurMsg",
                "Un compte existe déjà pour l'adresse " + emailTrim + ".");
            return "redirect:/secretariat";
        }
        // Créer le compte Utilisateur
        Utilisateur u = new Utilisateur();
        u.setNom(eleve.getNom());
        u.setPrenom(eleve.getPrenom());
        u.setEmail(emailTrim);
        u.setMotDePasse(passwordEncoder.encode(motDePasse));
        u.setRole("ELEVE");
        u.setEtablissement(etablissementService.getCurrentEtablissement());
        utilisateurRepository.save(u);

        // Lier l'élève à ce compte
        eleve.setCompteEmail(emailTrim);
        eleveRepository.save(eleve);

        ra.addFlashAttribute("successMsg",
            "Compte portail créé pour " + eleve.getNom() + " " + eleve.getPrenom()
            + " — email : " + emailTrim);
        return "redirect:/secretariat";
    }

    // ── Réinitialiser le mot de passe du compte portail d'un élève ───────
    @PostMapping("/eleves/{id}/reset-mdp")
    public String resetMdpEleve(@PathVariable Long id,
                                @RequestParam String motDePasse,
                                RedirectAttributes ra) {
        Eleve eleve = eleveRepository.findById(id).orElse(null);
        if (eleve == null || eleve.getCompteEmail() == null) {
            ra.addFlashAttribute("erreurMsg", "Élève ou compte introuvable.");
            return "redirect:/secretariat";
        }
        utilisateurRepository.findByEmail(eleve.getCompteEmail()).ifPresentOrElse(u -> {
            u.setMotDePasse(passwordEncoder.encode(motDePasse));
            utilisateurRepository.save(u);
            ra.addFlashAttribute("successMsg",
                "Mot de passe réinitialisé pour " + eleve.getNom() + " " + eleve.getPrenom() + ".");
        }, () -> ra.addFlashAttribute("erreurMsg", "Aucun compte trouvé pour cet élève."));
        return "redirect:/secretariat";
    }

    @PostMapping("/absences")
    public String ajouterAbsence(
            @RequestParam Long eleveId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) boolean estJustifiee,
            @RequestParam(required = false) String motif) {

        Eleve eleve = eleveRepository.findById(eleveId).orElseThrow();
        Absence absence = new Absence();
        absence.setEleve(eleve);
        absence.setDate(date);
        absence.setPeriode(periode);
        absence.setEstJustifiee(estJustifiee);
        absence.setMotif(motif);
        absenceRepository.save(absence);
        journalService.log("ABSENCE_SAISIE", "ABSENCES",
            eleve.getNom() + " " + eleve.getPrenom() + " — " + date);
        return "redirect:/secretariat";
    }

    @PostMapping("/absences/{id}/supprimer")
    public String supprimerAbsence(@PathVariable Long id) {
        absenceRepository.deleteById(id);
        return "redirect:/secretariat";
    }

    // ── Carte scolaire ───────────────────────────────────────────────────
    @GetMapping("/eleves/{id}/carte")
    public String carteScolaire(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Eleve eleve = eleveRepository.findById(id).orElse(null);
        if (eleve == null) {
            ra.addFlashAttribute("erreurMsg", "Élève introuvable.");
            return "redirect:/secretariat";
        }
        Long etabId = etablissementService.getCurrentEtablissementId();
        Map<String, String> params = parametreRepository.findByEtablissementId(etabId).stream()
            .collect(Collectors.toMap(Parametre::getCle, Parametre::getValeur, (a, b) -> a));
        model.addAttribute("eleve", eleve);
        model.addAttribute("nomEtab", params.getOrDefault("NOM_ETABLISSEMENT", "HolyFlame"));
        model.addAttribute("anneeScolaire", params.getOrDefault("ANNEE_SCOLAIRE", "2025-2026"));
        model.addAttribute("logoPath", params.getOrDefault("LOGO_ETAB", null));
        return "carte-scolaire";
    }

    @PostMapping("/eleves/{id}/carte/marquer-imprimee")
    @ResponseBody
    public ResponseEntity<Void> marquerCarteImprimee(@PathVariable Long id) {
        eleveRepository.findById(id).ifPresent(e -> {
            e.setCarteImprimee(true);
            eleveRepository.save(e);
        });
        return ResponseEntity.ok().build();
    }
}
