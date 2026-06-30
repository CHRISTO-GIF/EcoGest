package holyflame.administration.controller;

import holyflame.administration.model.*;
import holyflame.administration.repository.*;
import holyflame.administration.service.EtablissementService;
import holyflame.administration.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/notes")
public class NoteController {

    @Autowired private NoteRepository noteRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private MatiereRepository matiereRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private EnseignantAutorisationRepository autorisationRepository;
    @Autowired private EtablissementService etablissementService;
    @Autowired private JournalService journalService;

    // ──────────────────────────────────────────────────────────────
    // Helpers pour les autorisations enseignant
    // ──────────────────────────────────────────────────────────────

    private boolean isEnseignant() {
        Utilisateur u = etablissementService.getCurrentUtilisateur();
        return u != null && "ENSEIGNANT".equals(u.getRole());
    }

    /** Liste des autorisations du teacher connecté */
    private List<EnseignantAutorisation> getMyAutorisations() {
        Utilisateur u = etablissementService.getCurrentUtilisateur();
        if (u == null) return List.of();
        Long etabId = etablissementService.getCurrentEtablissementId();
        return autorisationRepository.findByEnseignantIdAndEtablissementId(u.getId(), etabId);
    }

    /** Vérifie que le teacher a le droit de saisir pour cette matière + classe */
    private boolean isAutorise(Long matiereId, Long classeId) {
        if (!isEnseignant()) return true; // ADMIN ou SECRETAIRE : accès total
        return getMyAutorisations().stream()
            .anyMatch(a -> a.getMatiereId().equals(matiereId) && a.getClasseId().equals(classeId));
    }

    // ──────────────────────────────────────────────────────────────
    // Vue liste des notes
    // ──────────────────────────────────────────────────────────────

    @GetMapping
    public String index(
            @RequestParam(required = false) Integer trimestre,
            @RequestParam(required = false) Long classeId,
            Model model) {

        Long etabId = etablissementService.getCurrentEtablissementId();
        List<Note> notes = noteRepository.findByEtablissementId(etabId);
        if (trimestre != null) {
            final int t = trimestre;
            notes = notes.stream().filter(n -> t == n.getTrimestre()).collect(Collectors.toList());
        }
        notes.sort((a, b) -> b.getDateEvaluation().compareTo(a.getDateEvaluation()));

        // Enseignant : filtrer les notes de ses classes autorisées
        if (isEnseignant()) {
            Set<Long> myClasseIds = getMyAutorisations().stream()
                .map(EnseignantAutorisation::getClasseId).collect(Collectors.toSet());
            notes = notes.stream()
                .filter(n -> n.getEleve() != null && n.getEleve().getClasse() != null
                          && myClasseIds.contains(n.getEleve().getClasse().getId()))
                .collect(Collectors.toList());
        }

        model.addAttribute("notes", notes);
        model.addAttribute("eleves",   eleveRepository.findByEtablissementIdOrderByNomAscPrenomAsc(etabId));
        model.addAttribute("matieres", matiereRepository.findByEtablissementIdOrderByNomAsc(etabId));
        model.addAttribute("classes",  classeRepository.findByEtablissementId(etabId));
        model.addAttribute("trimestreFiltre", trimestre);
        return "notes";
    }

    @PostMapping
    public String ajouterNote(
            @RequestParam Long eleveId,
            @RequestParam Long matiereId,
            @RequestParam Double valeur,
            @RequestParam(required = false) Double coefficient,
            @RequestParam String type,
            @RequestParam Integer trimestre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEvaluation,
            @RequestParam(required = false) String commentaire,
            RedirectAttributes ra) {

        Eleve eleve = eleveRepository.findById(eleveId).orElseThrow();
        // Sécurité : vérifier que l'enseignant est autorisé
        if (isEnseignant() && !isAutorise(matiereId, eleve.getClasse().getId())) {
            ra.addFlashAttribute("erreurAuth", "Vous n'êtes pas autorisé pour cette matière ou classe.");
            return "redirect:/notes";
        }

        Matiere matiere = matiereRepository.findById(matiereId).orElseThrow();
        Note note = new Note();
        note.setEleve(eleve); note.setMatiere(matiere);
        note.setValeur(valeur);
        note.setCoefficient(coefficient != null ? coefficient : matiere.getCoefficient());
        note.setType(type); note.setTrimestre(trimestre);
        note.setDateEvaluation(dateEvaluation != null ? dateEvaluation : LocalDate.now());
        note.setCommentaire(commentaire);
        note.setSaisieAt(LocalDateTime.now());
        Utilisateur saisieBy = etablissementService.getCurrentUtilisateur();
        if (saisieBy != null) note.setSaisieParId(saisieBy.getId());
        noteRepository.save(note);
        journalService.log("NOTE_SAISIE", "NOTES",
            eleve.getNom() + " " + eleve.getPrenom() + " — " + matiere.getNom() + " : " + valeur + "/20 (T" + trimestre + ")");
        return "redirect:/notes";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerNote(@PathVariable Long id) {
        noteRepository.findById(id).ifPresent(n -> journalService.log("NOTE_SUPPRIMÉE", "NOTES",
            (n.getEleve() != null ? n.getEleve().getNom() : "?") + " — "
            + (n.getMatiere() != null ? n.getMatiere().getNom() : "?") + " T" + n.getTrimestre()));
        noteRepository.deleteById(id);
        return "redirect:/notes";
    }

    // ──────────────────────────────────────────────────────────────
    // Saisie rapide (filtrée pour les enseignants)
    // ──────────────────────────────────────────────────────────────

    @GetMapping("/saisie")
    public String saisieForm(
            @RequestParam(required = false) Long matiereId,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Integer trimestre,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double coefficient,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEvaluation,
            Model model) {

        Long etabId = etablissementService.getCurrentEtablissementId();

        if (isEnseignant()) {
            // Filtrer matieres et classes selon autorisations
            List<EnseignantAutorisation> auths = getMyAutorisations();
            Set<Long> authMatiereIds = auths.stream().map(EnseignantAutorisation::getMatiereId).collect(Collectors.toSet());
            Set<Long> authClasseIds  = auths.stream().map(EnseignantAutorisation::getClasseId).collect(Collectors.toSet());

            List<Matiere> matieresFiltrees = matiereRepository.findByEtablissementIdOrderByNomAsc(etabId)
                .stream().filter(m -> authMatiereIds.contains(m.getId())).collect(Collectors.toList());
            List<Classe> classesFiltrees = classeRepository.findByEtablissementId(etabId)
                .stream().filter(c -> authClasseIds.contains(c.getId())).collect(Collectors.toList());

            model.addAttribute("matieres", matieresFiltrees);
            model.addAttribute("classes", classesFiltrees);

            // Mapping matière → classes autorisées pour filtrage JS dynamique
            Map<Long, List<Long>> matiereToClasses = new HashMap<>();
            for (EnseignantAutorisation a : auths) {
                matiereToClasses.computeIfAbsent(a.getMatiereId(), k -> new ArrayList<>()).add(a.getClasseId());
            }
            model.addAttribute("matiereToClasses", matiereToClasses);
            model.addAttribute("isEnseignant", true);
        } else {
            model.addAttribute("matieres", matiereRepository.findByEtablissementIdOrderByNomAsc(etabId));
            model.addAttribute("classes", classeRepository.findByEtablissementId(etabId));
            model.addAttribute("isEnseignant", false);
        }

        if (matiereId != null && classeId != null && trimestre != null) {
            // Vérification sécurité avant de charger les élèves
            if (isEnseignant() && !isAutorise(matiereId, classeId)) {
                model.addAttribute("erreurAuth", "Accès refusé pour cette matière ou classe.");
            } else {
                List<Eleve> eleves = eleveRepository.findByClasseIdOrderByNomAsc(classeId);
                Matiere matiere = matiereRepository.findById(matiereId).orElse(null);
                model.addAttribute("eleves", eleves);
                model.addAttribute("matiere", matiere);
                model.addAttribute("selectedMatiereId", matiereId);
                model.addAttribute("selectedClasseId", classeId);
                model.addAttribute("selectedTrimestre", trimestre);
                model.addAttribute("selectedType", type);
                model.addAttribute("selectedCoefficient", coefficient);
                model.addAttribute("selectedDate", dateEvaluation != null ? dateEvaluation : LocalDate.now());
            }
        }
        return "notes-saisie";
    }

    @PostMapping("/saisie/batch")
    public String saveBatch(
            @RequestParam Long matiereId,
            @RequestParam Long classeId,
            @RequestParam Integer trimestre,
            @RequestParam String type,
            @RequestParam Double coefficient,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEvaluation,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes ra) {

        // Sécurité : vérifier l'autorisation de l'enseignant
        if (isEnseignant() && !isAutorise(matiereId, classeId)) {
            ra.addFlashAttribute("erreurAuth", "Accès refusé : vous n'êtes pas autorisé pour cette matière ou classe.");
            return "redirect:/notes/saisie";
        }

        Matiere matiere = matiereRepository.findById(matiereId).orElseThrow();
        Utilisateur saisieBy = etablissementService.getCurrentUtilisateur();
        LocalDateTime now = LocalDateTime.now();
        int saved = 0;

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if (!key.startsWith("note_") || val == null || val.isBlank()) continue;
            try {
                Long eleveId = Long.parseLong(key.substring(5));
                double valeur = Double.parseDouble(val.replace(",", "."));
                if (valeur < 0 || valeur > 20) continue;
                Eleve eleve = eleveRepository.findById(eleveId).orElse(null);
                if (eleve == null) continue;

                Note note = new Note();
                note.setEleve(eleve); note.setMatiere(matiere);
                note.setValeur(valeur); note.setCoefficient(coefficient);
                note.setType(type); note.setTrimestre(trimestre);
                note.setDateEvaluation(dateEvaluation);
                note.setSaisieAt(now);
                if (saisieBy != null) note.setSaisieParId(saisieBy.getId());
                noteRepository.save(note);
                saved++;
            } catch (NumberFormatException ignored) {}
        }
        return "redirect:/notes/saisie?matiereId=" + matiereId
             + "&classeId=" + classeId + "&trimestre=" + trimestre
             + "&type=" + type + "&coefficient=" + coefficient
             + "&dateEvaluation=" + dateEvaluation + "&saved=" + saved;
    }

    @GetMapping("/bulletin/{eleveId}")
    public String bulletin(@PathVariable Long eleveId,
                           @RequestParam(defaultValue = "1") Integer trimestre,
                           Model model) {
        Eleve eleve = eleveRepository.findById(eleveId).orElseThrow();
        List<Note> notes = noteRepository.findByEleveAndTrimestreOrderByMatiereNomAsc(eleve, trimestre);
        double moyenneNum = notes.stream()
            .filter(n -> n.getValeur() != null && n.getCoefficient() != null)
            .mapToDouble(n -> n.getValeur() * n.getCoefficient()).sum();
        double totalCoef = notes.stream()
            .filter(n -> n.getCoefficient() != null)
            .mapToDouble(Note::getCoefficient).sum();
        double moyenneGenerale = totalCoef > 0 ? moyenneNum / totalCoef : 0;

        model.addAttribute("eleve", eleve);
        model.addAttribute("notes", notes);
        model.addAttribute("trimestre", trimestre);
        model.addAttribute("moyenneGenerale", Math.round(moyenneGenerale * 100.0) / 100.0);
        return "bulletin";
    }
}
