package holyflame.administration.controller;

import holyflame.administration.model.*;
import holyflame.administration.repository.*;
import holyflame.administration.service.EtablissementService;
import holyflame.administration.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/parametres")
public class ParametreController {

    @Autowired private ParametreRepository parametreRepository;
    @Autowired private FraisScolariteRepository fraisRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private MatiereRepository matiereRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private PersonnelRepository personnelRepository;
    @Autowired private EnseignantAutorisationRepository autorisationRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private EtablissementService etablissementService;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    public String index(Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();

        // Paramètres bruts
        Map<String, String> p = parametreRepository.findByEtablissementId(etabId).stream()
            .collect(Collectors.toMap(Parametre::getCle, Parametre::getValeur, (a, b) -> a));
        model.addAttribute("p", p);

        // Frais
        model.addAttribute("frais", fraisRepository.findByEtablissementIdOrderByTypeFraisAscDesignationAsc(etabId));

        // Classes année courante
        String annee = p.getOrDefault("ANNEE_SCOLAIRE", "2025-2026");
        model.addAttribute("classesAnnee", classeRepository.findByAnneeScolaireAndEtablissementId(annee, etabId));

        // ── Onglet Autorisations ──────────────────────────────────────────
        // Migration automatique des anciens enregistrements sans etabId
        matiereRepository.migrateNullEtablissementId(etabId);
        classeRepository.migrateNullEtablissementId(etabId);

        List<Matiere> matieres = matiereRepository.findByEtablissementIdOrderByNomAsc(etabId);
        List<Classe>  toutesClasses = classeRepository.findByEtablissementId(etabId);
        model.addAttribute("matieres", matieres);
        model.addAttribute("toutesClasses", toutesClasses);

        Map<Long, Matiere> matiereMap = matieres.stream().collect(Collectors.toMap(Matiere::getId, m -> m));
        Map<Long, Classe>  classeMap  = toutesClasses.stream().collect(Collectors.toMap(Classe::getId, c -> c));
        model.addAttribute("matiereMap", matiereMap);
        model.addAttribute("classeMap", classeMap);

        // Enseignants depuis Personnel (pas Utilisateur), avec compte associé par email
        List<Personnel> personnelsEnseignants = personnelRepository
            .findByFonctionAndEtablissementIdOrderByNomAsc("ENSEIGNANT", etabId);
        model.addAttribute("personnelsEnseignants", personnelsEnseignants);

        // Pour chaque personnel, trouver son compte Utilisateur par email
        Map<Long, Utilisateur> compteParPersonnelId = new HashMap<>();
        for (Personnel pers : personnelsEnseignants) {
            if (pers.getEmail() != null && !pers.getEmail().isBlank()) {
                utilisateurRepository.findByEmail(pers.getEmail())
                    .ifPresent(u -> compteParPersonnelId.put(pers.getId(), u));
            }
        }
        model.addAttribute("compteParPersonnelId", compteParPersonnelId);

        // Autorisations groupées par enseignantId (Utilisateur.id)
        List<EnseignantAutorisation> autorisations = autorisationRepository.findByEtablissementId(etabId);
        Map<Long, List<EnseignantAutorisation>> authByEnseignant = autorisations.stream()
            .collect(Collectors.groupingBy(EnseignantAutorisation::getEnseignantId));
        model.addAttribute("authByEnseignant", authByEnseignant);

        // ── Onglet Suivi ────────────────────────────────────────────────
        List<Map<String, Object>> suiviRows = buildSuiviRows(autorisations, matiereMap, classeMap, etabId);
        model.addAttribute("suiviRows", suiviRows);

        // Suivi par enseignant (TOUS les enseignants du personnel)
        List<Map<String, Object>> suiviEnseignants = buildSuiviEnseignants(
            personnelsEnseignants, compteParPersonnelId, autorisations, matiereMap, classeMap);
        model.addAttribute("suiviEnseignants", suiviEnseignants);

        // Délégation direction
        boolean delegationActive = "true".equals(p.get("DELEGATION_DIRECTION"));
        model.addAttribute("delegationActive", delegationActive);

        return "parametres";
    }

    private List<Map<String, Object>> buildSuiviRows(
            List<EnseignantAutorisation> autorisations,
            Map<Long, Matiere> matiereMap,
            Map<Long, Classe> classeMap,
            Long etabId) {

        List<Map<String, Object>> rows = new ArrayList<>();
        for (EnseignantAutorisation auth : autorisations) {
            Utilisateur enseignant = utilisateurRepository.findById(auth.getEnseignantId()).orElse(null);
            Matiere matiere = matiereMap.get(auth.getMatiereId());
            Classe  classe  = classeMap.get(auth.getClasseId());
            if (matiere == null || classe == null) continue;

            // Toutes les notes pour cette matière + classe, triées par saisieAt DESC
            List<Note> notes = noteRepository.findTopByMatiereAndClasse(auth.getMatiereId(), auth.getClasseId());
            long nbElevesSaisies = notes.stream()
                .map(n -> n.getEleve() != null ? n.getEleve().getId() : null)
                .filter(Objects::nonNull)
                .distinct().count();
            long totalEleves = eleveRepository.countByClasseId(auth.getClasseId());
            LocalDateTime derniereSaisie = notes.isEmpty() ? null : notes.get(0).getSaisieAt();

            String statut;
            if (notes.isEmpty()) {
                statut = "NON_REMPLI";
            } else if (totalEleves > 0 && nbElevesSaisies >= totalEleves) {
                statut = "COMPLET";
            } else {
                statut = "EN_COURS";
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("enseignantNom",    enseignant != null ? enseignant.getNom() + " " + enseignant.getPrenom() : "(compte supprimé)");
            row.put("matiereNom",       matiere.getNom());
            row.put("classeNom",        classe.getNom());
            row.put("derniereSaisie",   derniereSaisie);
            row.put("nbNotes",          notes.size());
            row.put("nbElevesSaisies",  nbElevesSaisies);
            row.put("totalEleves",      totalEleves);
            row.put("statut",           statut);
            rows.add(row);
        }
        return rows;
    }

    private static final List<String> EXAM_TYPES = List.of("DEVOIR", "EXAMEN", "PARTICIPATION");

    private List<Map<String, Object>> buildSuiviEnseignants(
            List<Personnel> enseignants,
            Map<Long, Utilisateur> compteParPersonnelId,
            List<EnseignantAutorisation> toutesAutorisations,
            Map<Long, Matiere> matiereMap,
            Map<Long, Classe> classeMap) {

        List<Map<String, Object>> rows = new ArrayList<>();

        for (Personnel pers : enseignants) {
            Utilisateur compte = compteParPersonnelId.get(pers.getId());

            List<EnseignantAutorisation> authsPers = compte != null
                ? toutesAutorisations.stream()
                    .filter(a -> a.getEnseignantId().equals(compte.getId()))
                    .toList()
                : List.of();

            if (authsPers.isEmpty()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("enseignantNom", pers.getNom() + " " + pers.getPrenom());
                row.put("hasCompte", compte != null);
                row.put("matiereNom", null);
                row.put("classeNom", null);
                row.put("totalEleves", 0L);
                rows.add(row);
            } else {
                for (EnseignantAutorisation auth : authsPers) {
                    Matiere matiere = matiereMap.get(auth.getMatiereId());
                    Classe  classe  = classeMap.get(auth.getClasseId());
                    if (matiere == null || classe == null) continue;

                    long totalEleves = eleveRepository.countByClasseId(auth.getClasseId());

                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("enseignantNom", pers.getNom() + " " + pers.getPrenom());
                    row.put("hasCompte", compte != null);
                    row.put("matiereNom", matiere.getNom());
                    row.put("classeNom", classe.getNom());
                    row.put("totalEleves", totalEleves);

                    for (String type : EXAM_TYPES) {
                        for (int t = 1; t <= 3; t++) {
                            long nb = noteRepository.countElevesByMatiereClasseTypeTrimestre(
                                auth.getMatiereId(), auth.getClasseId(), type, t);
                            String statut = nb == 0 ? "VIDE"
                                : (totalEleves > 0 && nb >= totalEleves ? "COMPLET" : "EN_COURS");
                            row.put(type + "_T" + t, statut);
                            row.put(type + "_T" + t + "_nb", nb);
                        }
                    }
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    @PostMapping("/update")
    public String update(@RequestParam Map<String, String> formParams, RedirectAttributes ra) {
        String tab = formParams.getOrDefault("_tab", "etablissement");

        if ("etablissement".equals(tab)) {
            List.of("TYPE_MATERNELLE","TYPE_PRIMAIRE","TYPE_COLLEGE","TYPE_LYCEE","TYPE_LYCEE_GENERAL")
                .forEach(key -> upsertParam(key, "false", key, "ETABLISSEMENT"));
        }

        formParams.forEach((cle, valeur) -> {
            if (!cle.startsWith("_")) {
                upsertParam(cle, valeur, cle, tab.toUpperCase());
            }
        });

        ra.addFlashAttribute("savedTab", tab);
        return "redirect:/parametres?saved=true&tab=" + tab;
    }

    @PostMapping("/delegation")
    public String toggleDelegation(@RequestParam(defaultValue = "false") boolean delegationDirection,
                                   RedirectAttributes ra) {
        upsertParam("DELEGATION_DIRECTION", String.valueOf(delegationDirection),
                    "Délégation accès direction", "SECURITE");
        ra.addFlashAttribute("successMsg", "Paramètre de délégation mis à jour.");
        return "redirect:/parametres?saved=true&tab=autorisations";
    }

    @PostMapping("/classes/generer")
    public String genererClasses(RedirectAttributes ra) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        String annee = parametreRepository.findByCleAndEtablissementId("ANNEE_SCOLAIRE", etabId)
            .map(Parametre::getValeur).orElse("2025-2026");

        Map<String, List<String>> mapping = new LinkedHashMap<>();
        mapping.put("TYPE_MATERNELLE",   List.of("Petite Section", "Moyenne Section", "Grande Section"));
        mapping.put("TYPE_PRIMAIRE",     List.of("CP1", "CP2", "CE1", "CE2", "CM1", "CM2"));
        mapping.put("TYPE_COLLEGE",      List.of("6ème", "5ème", "4ème", "3ème"));
        mapping.put("TYPE_LYCEE",        List.of("2nde", "1ère", "Terminale"));
        mapping.put("TYPE_LYCEE_GENERAL",List.of("2nde G", "1ère G", "Terminale G"));

        int count = 0;
        for (Map.Entry<String, List<String>> e : mapping.entrySet()) {
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
        return "redirect:/parametres?saved=true&tab=etablissement";
    }

    @PostMapping("/frais")
    public String ajouterFrais(
            @RequestParam String designation, @RequestParam String typeFrais,
            @RequestParam Double montant, @RequestParam String echeance,
            @RequestParam(required = false) String niveauCible,
            @RequestParam(required = false) boolean obligatoire) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        FraisScolarite f = new FraisScolarite();
        f.setDesignation(designation); f.setTypeFrais(typeFrais);
        f.setMontant(montant); f.setEcheance(echeance);
        f.setNiveauCible(niveauCible); f.setObligatoire(obligatoire);
        f.setEtablissementId(etabId);
        fraisRepository.save(f);
        return "redirect:/parametres?saved=true&tab=frais";
    }

    @PostMapping("/frais/{id}/modifier")
    public String modifierFrais(@PathVariable Long id,
            @RequestParam String designation, @RequestParam String typeFrais,
            @RequestParam Double montant, @RequestParam String echeance,
            @RequestParam(required = false) String niveauCible,
            @RequestParam(required = false) boolean obligatoire) {
        FraisScolarite f = fraisRepository.findById(id).orElseThrow();
        f.setDesignation(designation); f.setTypeFrais(typeFrais);
        f.setMontant(montant); f.setEcheance(echeance);
        f.setNiveauCible(niveauCible); f.setObligatoire(obligatoire);
        fraisRepository.save(f);
        return "redirect:/parametres?saved=true&tab=frais";
    }

    @PostMapping("/frais/{id}/supprimer")
    public String supprimerFrais(@PathVariable Long id) {
        fraisRepository.deleteById(id);
        return "redirect:/parametres?tab=frais";
    }

    // ── Personnel : créer un compte Utilisateur ENSEIGNANT ───────────────
    @PostMapping("/personnel/{id}/creer-compte")
    public String creerCompte(@PathVariable Long id,
                              @RequestParam String motDePasse,
                              RedirectAttributes ra) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        Personnel pers = personnelRepository.findById(id).orElse(null);
        if (pers == null) {
            ra.addFlashAttribute("erreurMsg", "Personnel introuvable.");
            return "redirect:/parametres?tab=autorisations";
        }
        if (pers.getEmail() == null || pers.getEmail().isBlank()) {
            ra.addFlashAttribute("erreurMsg",
                pers.getNom() + " " + pers.getPrenom() + " n'a pas d'email — ajoutez-en un dans sa fiche Personnel d'abord.");
            return "redirect:/parametres?tab=autorisations";
        }
        if (utilisateurRepository.findByEmail(pers.getEmail()).isPresent()) {
            ra.addFlashAttribute("erreurMsg",
                "Un compte existe déjà pour l'email " + pers.getEmail() + ".");
            return "redirect:/parametres?tab=autorisations";
        }
        Etablissement etab = etablissementService.getCurrentEtablissement();
        Utilisateur u = new Utilisateur();
        u.setNom(pers.getNom());
        u.setPrenom(pers.getPrenom());
        u.setEmail(pers.getEmail());
        u.setMotDePasse(passwordEncoder.encode(motDePasse));
        u.setRole("ENSEIGNANT");
        u.setEtablissement(etab);
        utilisateurRepository.save(u);
        ra.addFlashAttribute("successMsg",
            "Compte créé pour " + pers.getNom() + " " + pers.getPrenom()
            + " — email : " + pers.getEmail() + ", rôle : ENSEIGNANT.");
        return "redirect:/parametres?tab=autorisations";
    }

    // ── Personnel : réinitialiser le mot de passe d'un compte existant ──
    @PostMapping("/personnel/{id}/reset-mdp")
    public String resetMotDePasse(@PathVariable Long id,
                                  @RequestParam String motDePasse,
                                  RedirectAttributes ra) {
        Personnel pers = personnelRepository.findById(id).orElse(null);
        if (pers == null || pers.getEmail() == null) {
            ra.addFlashAttribute("erreurMsg", "Personnel ou email introuvable.");
            return "redirect:/parametres?tab=autorisations";
        }
        utilisateurRepository.findByEmail(pers.getEmail()).ifPresentOrElse(u -> {
            u.setMotDePasse(passwordEncoder.encode(motDePasse));
            utilisateurRepository.save(u);
            ra.addFlashAttribute("successMsg",
                "Mot de passe réinitialisé pour " + pers.getNom() + " " + pers.getPrenom() + ".");
        }, () -> ra.addFlashAttribute("erreurMsg", "Aucun compte trouvé pour cet enseignant."));
        return "redirect:/parametres?tab=autorisations";
    }

    // ── Personnel : mise à jour du codeAcces ───────────────────────────
    @PostMapping("/personnel/{id}/codeAcces")
    public String updateCodeAcces(@PathVariable Long id,
                                  @RequestParam String codeAcces,
                                  RedirectAttributes ra) {
        personnelRepository.findById(id).ifPresent(p -> {
            p.setCodeAcces(codeAcces.isBlank() ? null : codeAcces.trim());
            personnelRepository.save(p);
        });
        ra.addFlashAttribute("successMsg", "Code d'accès mis à jour.");
        return "redirect:/parametres?tab=autorisations";
    }

    // ── Upload logo établissement ───────────────────────────────────
    @PostMapping(value = "/logo", consumes = "multipart/form-data")
    public String uploadLogo(@RequestParam("logo") MultipartFile logo, RedirectAttributes ra) {
        if (logo.isEmpty()) {
            ra.addFlashAttribute("erreurMsg", "Veuillez sélectionner un fichier image.");
            return "redirect:/parametres?tab=etablissement";
        }
        String ct = logo.getContentType() != null ? logo.getContentType() : "";
        if (!ct.startsWith("image/")) {
            ra.addFlashAttribute("erreurMsg", "Fichier invalide : seules les images sont acceptées (PNG, JPG, SVG).");
            return "redirect:/parametres?tab=etablissement";
        }
        try {
            String path = fileStorageService.store(logo, "logos");
            upsertParam("LOGO_ETAB", path, "Logo de l'établissement", "ETABLISSEMENT");
            ra.addFlashAttribute("successMsg", "Logo mis à jour avec succès.");
        } catch (java.io.IOException e) {
            ra.addFlashAttribute("erreurMsg", "Erreur lors de l'enregistrement du logo : " + e.getMessage());
        }
        return "redirect:/parametres?tab=etablissement";
    }

    private void upsertParam(String cle, String valeur, String desc, String cat) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        Parametre p = parametreRepository.findByCleAndEtablissementId(cle, etabId)
            .orElseGet(() -> {
                Parametre np = new Parametre();
                np.setCle(cle); np.setDescription(desc);
                np.setCategorie(cat); np.setEtablissementId(etabId);
                return np;
            });
        p.setValeur(valeur);
        parametreRepository.save(p);
    }
}
