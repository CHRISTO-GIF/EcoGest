package holyflame.administration.controller;

import holyflame.administration.model.Eleve;
import holyflame.administration.model.LigneBudget;
import holyflame.administration.model.Paiement;
import holyflame.administration.repository.EleveRepository;
import holyflame.administration.repository.LigneBudgetRepository;
import holyflame.administration.repository.PaiementRepository;
import holyflame.administration.repository.ParametreRepository;
import holyflame.administration.service.EtablissementService;
import holyflame.administration.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/finances")
public class FinancesController {

    @Autowired private PaiementRepository paiementRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private LigneBudgetRepository budgetRepository;
    @Autowired private ParametreRepository parametreRepository;
    @Autowired private EtablissementService etablissementService;
    @Autowired private JournalService journalService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "2025-2026") String annee, Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        List<Paiement> paiements = paiementRepository.findByEtablissementId(etabId);
        double totalEncaisse = paiements.stream()
            .mapToDouble(p -> p.getMontantVerse() != null ? p.getMontantVerse() : 0).sum();

        List<LigneBudget> lignes = budgetRepository.findByEtablissementIdAndAnneeScolaireOrderByCategorieAscDesignationAsc(etabId, annee);
        double totalRevenuPrevu = lignes.stream().filter(l -> "REVENU".equals(l.getCategorie()))
            .mapToDouble(l -> l.getMontantPrevu() != null ? l.getMontantPrevu() : 0).sum();
        double totalDepensePrevu = lignes.stream().filter(l -> "DEPENSE".equals(l.getCategorie()))
            .mapToDouble(l -> l.getMontantPrevu() != null ? l.getMontantPrevu() : 0).sum();
        double totalRevenuReel = lignes.stream().filter(l -> "REVENU".equals(l.getCategorie()))
            .mapToDouble(l -> l.getMontantReel() != null ? l.getMontantReel() : 0).sum();
        double totalDepenseReel = lignes.stream().filter(l -> "DEPENSE".equals(l.getCategorie()))
            .mapToDouble(l -> l.getMontantReel() != null ? l.getMontantReel() : 0).sum();

        model.addAttribute("paiements",       paiements);
        model.addAttribute("eleves",          eleveRepository.findByEtablissementIdOrderByNomAscPrenomAsc(etabId));
        model.addAttribute("totalEncaisse",   totalEncaisse);
        model.addAttribute("lignes",          lignes);
        model.addAttribute("annee",           annee);
        model.addAttribute("totalRevenuPrevu",  totalRevenuPrevu);
        model.addAttribute("totalDepensePrevu", totalDepensePrevu);
        model.addAttribute("totalRevenuReel",   totalRevenuReel);
        model.addAttribute("totalDepenseReel",  totalDepenseReel);
        model.addAttribute("soldePrevu",      totalRevenuPrevu - totalDepensePrevu);
        model.addAttribute("soldeReel",       totalRevenuReel  - totalDepenseReel);
        model.addAttribute("tauxEncaissement", totalRevenuPrevu > 0 ? totalEncaisse / totalRevenuPrevu * 100 : 0);
        return "finances";
    }

    // ===== PAIEMENTS =====
    @PostMapping("/paiements")
    public String enregistrerPaiement(
            @RequestParam Long eleveId,
            @RequestParam Double montantVerse,
            @RequestParam String typePaiement,
            @RequestParam String modePaiement,
            @RequestParam(required = false) String recuNumero) {

        Eleve eleve = eleveRepository.findById(eleveId).orElseThrow();
        Paiement p = new Paiement();
        p.setEleve(eleve); p.setMontantVerse(montantVerse);
        p.setTypePaiement(typePaiement); p.setModePaiement(modePaiement);
        p.setDatePaiement(LocalDateTime.now());
        p.setRecuNumero(recuNumero != null && !recuNumero.isBlank() ? recuNumero : "HF-" + System.currentTimeMillis());
        paiementRepository.save(p);
        journalService.log("PAIEMENT_ENREGISTRÉ", "FINANCES",
            eleve.getNom() + " " + eleve.getPrenom() + " — " + montantVerse + " F (" + typePaiement + ")");
        return "redirect:/finances/paiements/" + p.getId() + "/recu";
    }

    @GetMapping("/paiements/{id}/recu")
    public String recu(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Long etabId = etablissementService.getCurrentEtablissementId();
        Paiement p = paiementRepository.findById(id).orElse(null);
        if (p == null) {
            ra.addFlashAttribute("erreur", "Paiement introuvable.");
            return "redirect:/finances?tab=paiements";
        }

        Map<String, String> params = parametreRepository.findByEtablissementId(etabId).stream()
            .collect(Collectors.toMap(
                holyflame.administration.model.Parametre::getCle,
                holyflame.administration.model.Parametre::getValeur,
                (a, b) -> a));

        model.addAttribute("paiement",   p);
        model.addAttribute("nomEtab",    params.getOrDefault("NOM_ETABLISSEMENT", "HolyFlame"));
        model.addAttribute("adresseEtab",params.getOrDefault("ADRESSE_ETABLISSEMENT", ""));
        model.addAttribute("telEtab",    params.getOrDefault("TEL_ETABLISSEMENT", ""));
        model.addAttribute("anneeScolaire", params.getOrDefault("ANNEE_SCOLAIRE", "2025-2026"));
        model.addAttribute("logoPath",   params.getOrDefault("LOGO_ETAB", null));
        return "recu-paiement";
    }

    @PostMapping("/paiements/{id}/supprimer")
    public String supprimerPaiement(@PathVariable Long id) {
        paiementRepository.findById(id).ifPresent(p -> journalService.log("PAIEMENT_SUPPRIMÉ", "FINANCES",
            "Reçu " + (p.getRecuNumero() != null ? p.getRecuNumero() : id)));
        paiementRepository.deleteById(id);
        return "redirect:/finances?tab=paiements";
    }

    // ===== BUDGET =====
    @PostMapping("/budget")
    public String ajouterBudget(
            @RequestParam String designation,
            @RequestParam String categorie,
            @RequestParam String typeLigne,
            @RequestParam Double montantPrevu,
            @RequestParam(required = false) Double montantReel,
            @RequestParam(required = false) Integer mois,
            @RequestParam(defaultValue = "2025-2026") String anneeScolaire,
            @RequestParam(required = false) String notes) {

        LigneBudget l = new LigneBudget();
        l.setDesignation(designation); l.setCategorie(categorie); l.setTypeLigne(typeLigne);
        l.setMontantPrevu(montantPrevu); l.setMontantReel(montantReel); l.setMois(mois);
        l.setAnneeScolaire(anneeScolaire); l.setNotes(notes); l.setDateCreation(LocalDate.now());
        budgetRepository.save(l);
        return "redirect:/finances?tab=budget&annee=" + anneeScolaire;
    }

    @PostMapping("/budget/{id}/supprimer")
    public String supprimerBudget(@PathVariable Long id) {
        String annee = budgetRepository.findById(id).map(LigneBudget::getAnneeScolaire).orElse("2025-2026");
        budgetRepository.deleteById(id);
        return "redirect:/finances?tab=budget&annee=" + annee;
    }

    @PostMapping("/budget/{id}/realiser")
    public String realiserBudget(@PathVariable Long id, @RequestParam Double montantReel) {
        budgetRepository.findById(id).ifPresent(l -> { l.setMontantReel(montantReel); budgetRepository.save(l); });
        return "redirect:/finances?tab=budget";
    }
}
