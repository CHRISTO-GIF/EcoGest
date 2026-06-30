package holyflame.administration.controller;

import holyflame.administration.model.Absence;
import holyflame.administration.model.Classe;
import holyflame.administration.model.Eleve;
import holyflame.administration.model.Programme;
import holyflame.administration.repository.AbsenceRepository;
import holyflame.administration.repository.ClasseRepository;
import holyflame.administration.repository.EleveRepository;
import holyflame.administration.repository.ProgrammeRepository;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/surveillance")
public class SurveillanceController {

    @Autowired private AbsenceRepository absenceRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private ProgrammeRepository programmeRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String index(Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        model.addAttribute("absences",  absenceRepository.findByEtablissementId(etabId));
        model.addAttribute("eleves",    eleveRepository.findByEtablissementIdOrderByNomAscPrenomAsc(etabId));
        model.addAttribute("programmes", programmeRepository.findAllByOrderByDateDebutDesc());
        model.addAttribute("classes",   classeRepository.findByEtablissementId(etabId));
        return "surveillance";
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
        absence.setPeriode(periode != null ? periode : "Journée entière");
        absence.setEstJustifiee(estJustifiee);
        absence.setMotif(motif);
        absenceRepository.save(absence);
        return "redirect:/surveillance";
    }

    @PostMapping("/absences/{id}/supprimer")
    public String supprimerAbsence(@PathVariable Long id) {
        absenceRepository.deleteById(id);
        return "redirect:/surveillance";
    }

    @PostMapping("/programmes")
    public String creerProgramme(
            @RequestParam String titre,
            @RequestParam(required = false) String contenu,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long classeId,
            Authentication auth) {

        Programme programme = new Programme();
        programme.setTitre(titre);
        programme.setContenu(contenu);
        programme.setDateDebut(dateDebut);
        programme.setDateFin(dateFin);
        programme.setStatut("BROUILLON");
        programme.setAuteur(auth.getName());
        if (classeId != null) {
            classeRepository.findById(classeId).ifPresent(programme::setClasse);
        }
        programmeRepository.save(programme);
        return "redirect:/surveillance";
    }

    @PostMapping("/programmes/{id}/publier")
    public String togglePublier(@PathVariable Long id) {
        Programme programme = programmeRepository.findById(id).orElseThrow();
        programme.setStatut("BROUILLON".equals(programme.getStatut()) ? "PUBLIE" : "BROUILLON");
        programmeRepository.save(programme);
        return "redirect:/surveillance";
    }

    @PostMapping("/programmes/{id}/supprimer")
    public String supprimerProgramme(@PathVariable Long id) {
        programmeRepository.deleteById(id);
        return "redirect:/surveillance";
    }
}
