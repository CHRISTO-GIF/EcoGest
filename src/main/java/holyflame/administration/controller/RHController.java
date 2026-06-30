package holyflame.administration.controller;

import holyflame.administration.model.*;
import holyflame.administration.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/rh")
public class RHController {

    @Autowired private PersonnelRepository personnelRepository;
    @Autowired private ContratRepository contratRepository;
    @Autowired private CongeRepository congeRepository;
    @Autowired private SalaireMensuelRepository salaireRepository;

    @GetMapping
    public String index() {
        return "redirect:/personnel";
    }

    // ===== CONTRATS =====
    @PostMapping("/contrats")
    public String ajouterContrat(
            @RequestParam Long personnelId,
            @RequestParam String typeContrat,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam Double salaireBase,
            @RequestParam(required = false) String notes) {

        Contrat c = new Contrat();
        c.setPersonnel(personnelRepository.findById(personnelId).orElseThrow());
        c.setTypeContrat(typeContrat); c.setDateDebut(dateDebut); c.setDateFin(dateFin);
        c.setSalaireBase(salaireBase); c.setStatut("ACTIF"); c.setNotes(notes);
        contratRepository.save(c);
        return "redirect:/personnel/" + personnelId + "?saved=true#rh-contrats";
    }

    @PostMapping("/contrats/{id}/supprimer")
    public String supprimerContrat(@PathVariable Long id) {
        Long pid = contratRepository.findById(id).map(c -> c.getPersonnel().getId()).orElse(null);
        contratRepository.deleteById(id);
        return pid != null ? "redirect:/personnel/" + pid + "#rh-contrats" : "redirect:/personnel";
    }

    // ===== CONGÉS =====
    @PostMapping("/conges")
    public String ajouterConge(
            @RequestParam Long personnelId,
            @RequestParam String typeConge,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String motif) {

        Conge c = new Conge();
        c.setPersonnel(personnelRepository.findById(personnelId).orElseThrow());
        c.setTypeConge(typeConge); c.setDateDebut(dateDebut); c.setDateFin(dateFin);
        c.setJoursNombre((int) ChronoUnit.DAYS.between(dateDebut, dateFin) + 1);
        c.setStatut("EN_ATTENTE"); c.setMotif(motif);
        congeRepository.save(c);
        return "redirect:/personnel/" + personnelId + "?saved=true#rh-conges";
    }

    @PostMapping("/conges/{id}/approuver")
    public String approuverConge(@PathVariable Long id) {
        Long pid = congeRepository.findById(id).map(c -> c.getPersonnel().getId()).orElse(null);
        congeRepository.findById(id).ifPresent(c -> { c.setStatut("APPROUVE"); congeRepository.save(c); });
        return pid != null ? "redirect:/personnel/" + pid + "#rh-conges" : "redirect:/personnel";
    }

    @PostMapping("/conges/{id}/refuser")
    public String refuserConge(@PathVariable Long id) {
        Long pid = congeRepository.findById(id).map(c -> c.getPersonnel().getId()).orElse(null);
        congeRepository.findById(id).ifPresent(c -> { c.setStatut("REFUSE"); congeRepository.save(c); });
        return pid != null ? "redirect:/personnel/" + pid + "#rh-conges" : "redirect:/personnel";
    }

    @PostMapping("/conges/{id}/supprimer")
    public String supprimerConge(@PathVariable Long id) {
        Long pid = congeRepository.findById(id).map(c -> c.getPersonnel().getId()).orElse(null);
        congeRepository.deleteById(id);
        return pid != null ? "redirect:/personnel/" + pid + "#rh-conges" : "redirect:/personnel";
    }

    // ===== SALAIRES =====
    @PostMapping("/salaires")
    public String ajouterSalaire(
            @RequestParam Long personnelId,
            @RequestParam int mois,
            @RequestParam int annee,
            @RequestParam Double salaireBase,
            @RequestParam(defaultValue = "0") Double primes,
            @RequestParam(defaultValue = "0") Double retenues) {

        SalaireMensuel s = new SalaireMensuel();
        s.setPersonnel(personnelRepository.findById(personnelId).orElseThrow());
        s.setMois(mois); s.setAnnee(annee); s.setSalaireBase(salaireBase);
        s.setPrimes(primes); s.setRetenues(retenues);
        s.setNet(salaireBase + primes - retenues); s.setStatut("EN_ATTENTE");
        salaireRepository.save(s);
        return "redirect:/personnel/" + personnelId + "?saved=true#rh-salaires";
    }

    @PostMapping("/salaires/{id}/payer")
    public String payerSalaire(@PathVariable Long id) {
        Long pid = salaireRepository.findById(id).map(s -> s.getPersonnel().getId()).orElse(null);
        salaireRepository.findById(id).ifPresent(s -> {
            s.setStatut("PAYE"); s.setDatePaiement(LocalDate.now()); salaireRepository.save(s);
        });
        return pid != null ? "redirect:/personnel/" + pid + "#rh-salaires" : "redirect:/personnel";
    }

    @PostMapping("/salaires/{id}/supprimer")
    public String supprimerSalaire(@PathVariable Long id) {
        Long pid = salaireRepository.findById(id).map(s -> s.getPersonnel().getId()).orElse(null);
        salaireRepository.deleteById(id);
        return pid != null ? "redirect:/personnel/" + pid + "#rh-salaires" : "redirect:/personnel";
    }
}
