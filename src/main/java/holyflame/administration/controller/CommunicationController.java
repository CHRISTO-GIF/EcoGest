package holyflame.administration.controller;

import holyflame.administration.model.CommunicationMessage;
import holyflame.administration.model.ModeleMessage;
import holyflame.administration.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/communication")
public class CommunicationController {

    @Autowired private CommunicationMessageRepository commRepository;
    @Autowired private ModeleMessageRepository modeleRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private PersonnelRepository personnelRepository;
    @Autowired private ClasseRepository classeRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("historique", commRepository.findAllByOrderByDateEnvoiDesc());
        model.addAttribute("modeles",    modeleRepository.findAllByOrderByTypeAscNomAsc());
        model.addAttribute("classes",    classeRepository.findAll());
        model.addAttribute("eleves",     eleveRepository.findAllByOrderByNomAscPrenomAsc());
        model.addAttribute("personnels", personnelRepository.findAllByOrderByNomAscPrenomAsc());
        model.addAttribute("totalEnvoye", commRepository.findByStatutOrderByDateEnvoiDesc("ENVOYE").size());
        return "communication";
    }

    @PostMapping("/envoyer")
    public String envoyer(
            @RequestParam String type,
            @RequestParam String sujet,
            @RequestParam String contenu,
            @RequestParam String cibleType,
            @RequestParam(required = false) Long classeId,
            Authentication auth) {

        List<?> destinataires;
        String destStr;
        int nb;

        if ("TOUS_PARENTS".equals(cibleType)) {
            List<String> emails = eleveRepository.findAllByOrderByNomAscPrenomAsc().stream()
                .map(e -> e.getEmailParent() != null ? e.getEmailParent() : "")
                .filter(s -> !s.isBlank()).distinct().toList();
            destStr = String.join(", ", emails);
            nb = emails.size();
        } else if ("CLASSE".equals(cibleType) && classeId != null) {
            List<String> emails = eleveRepository.findByClasseIdOrderByNomAsc(classeId).stream()
                .map(e -> e.getEmailParent() != null ? e.getEmailParent() : "")
                .filter(s -> !s.isBlank()).distinct().toList();
            destStr = String.join(", ", emails);
            nb = emails.size();
        } else if ("PERSONNEL".equals(cibleType)) {
            List<String> emails = personnelRepository.findAllByOrderByNomAscPrenomAsc().stream()
                .map(p -> p.getEmail() != null ? p.getEmail() : "")
                .filter(s -> !s.isBlank()).toList();
            destStr = String.join(", ", emails);
            nb = emails.size();
        } else {
            destStr = "";
            nb = 0;
        }

        CommunicationMessage msg = new CommunicationMessage();
        msg.setType(type); msg.setSujet(sujet); msg.setContenu(contenu);
        msg.setCibleType(cibleType); msg.setDestinataires(destStr);
        msg.setNbDestinataires(nb);
        msg.setExpediteur(auth != null ? auth.getName() : "système");
        msg.setDateEnvoi(LocalDateTime.now());
        // Simulated send: mark ENVOYE (configure SMTP in application.properties for real emails)
        msg.setStatut(nb > 0 ? "ENVOYE" : "ECHEC");
        commRepository.save(msg);
        return "redirect:/communication?sent=" + nb;
    }

    @PostMapping("/modeles")
    public String ajouterModele(
            @RequestParam String nom,
            @RequestParam String type,
            @RequestParam(required = false) String sujet,
            @RequestParam String contenu) {

        ModeleMessage m = new ModeleMessage();
        m.setNom(nom); m.setType(type); m.setSujet(sujet); m.setContenu(contenu);
        modeleRepository.save(m);
        return "redirect:/communication#modeles";
    }

    @PostMapping("/modeles/{id}/supprimer")
    public String supprimerModele(@PathVariable Long id) {
        modeleRepository.deleteById(id);
        return "redirect:/communication#modeles";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerMessage(@PathVariable Long id) {
        commRepository.deleteById(id);
        return "redirect:/communication";
    }
}
