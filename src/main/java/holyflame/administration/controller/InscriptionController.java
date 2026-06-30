package holyflame.administration.controller;

import holyflame.administration.model.Etablissement;
import holyflame.administration.model.Utilisateur;
import holyflame.administration.repository.EtablissementRepository;
import holyflame.administration.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class InscriptionController {

    @Autowired private EtablissementRepository etablissementRepository;
    @Autowired private UtilisateurRepository   utilisateurRepository;
    @Autowired private PasswordEncoder          passwordEncoder;

    @GetMapping("/inscription")
    public String formulaire() {
        return "inscription";
    }

    @PostMapping("/inscription")
    public String creer(
            // ── Étape 1 : l'école ──────────────────────────────
            @RequestParam String nomEcole,
            @RequestParam(required = false) String adresseEcole,
            @RequestParam(required = false) String telephoneEcole,
            @RequestParam(required = false) String emailEcole,

            // ── Étape 2 : admin + localisation ─────────────────
            @RequestParam String adminNom,
            @RequestParam String adminPrenom,
            @RequestParam String adminEmail,
            @RequestParam String adminMotDePasse,
            @RequestParam(required = false) String indicatifPays,
            @RequestParam(required = false) String adminTelephone,
            @RequestParam(required = false) String pays,
            @RequestParam(required = false) String monnaie,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String typeEtablissement,
            @RequestParam(required = false) String typeGestion,
            @RequestParam(required = false) String anneeScolaire,

            RedirectAttributes ra) {

        // Vérifier que l'email admin n'est pas déjà utilisé
        if (utilisateurRepository.findByEmail(adminEmail.trim().toLowerCase()).isPresent()) {
            ra.addFlashAttribute("erreurInscription",
                "L'adresse email " + adminEmail + " est déjà associée à un compte. Utilisez une autre adresse.");
            return "redirect:/inscription";
        }

        // ── Créer l'établissement ───────────────────────────────
        Etablissement etab = new Etablissement();
        etab.setNom(nomEcole.trim());
        etab.setAdresse(adresseEcole);
        etab.setTelephone(telephoneEcole);
        etab.setEmail(emailEcole);
        etab.setContact(adminNom.toUpperCase() + " " + adminPrenom);
        etab.setPays(pays);
        etab.setProvince(province);
        etab.setVille(province);
        etab.setMonnaie(monnaie != null && !monnaie.isBlank() ? monnaie : "FCFA");
        etab.setTypeEtablissement(typeEtablissement);
        etab.setTypeGestion(typeGestion);
        etab.setIndicatifPays(indicatifPays);
        etab.setTelephoneAdmin(
            indicatifPays != null && adminTelephone != null
            ? indicatifPays + " " + adminTelephone
            : adminTelephone);
        etab.setAnneeScolaire(
            anneeScolaire != null && !anneeScolaire.isBlank()
            ? anneeScolaire
            : LocalDate.now().getYear() + "-" + (LocalDate.now().getYear() + 1));
        etab.setStatut("ACTIF");
        etab.setDateCreation(LocalDate.now());
        String code = "HF-" + LocalDate.now().getYear() + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        etab.setCodeAcces(code);
        etablissementRepository.save(etab);

        // ── Créer le compte administrateur ─────────────────────
        Utilisateur admin = new Utilisateur();
        admin.setNom(adminNom.toUpperCase().trim());
        admin.setPrenom(adminPrenom.trim());
        admin.setEmail(adminEmail.trim().toLowerCase());
        admin.setMotDePasse(passwordEncoder.encode(adminMotDePasse));
        admin.setRole("ADMIN");
        admin.setEtablissement(etab);
        utilisateurRepository.save(admin);

        // ── Rediriger vers le login avec message de succès ──────
        ra.addFlashAttribute("inscriptionSucces", true);
        ra.addFlashAttribute("adminEmail", adminEmail.trim().toLowerCase());
        ra.addFlashAttribute("nomEcole", nomEcole.trim());
        return "redirect:/login";
    }
}
