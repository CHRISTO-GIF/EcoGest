package holyflame.administration.controller;

import holyflame.administration.model.*;
import holyflame.administration.repository.*;
import holyflame.administration.service.EtablissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tableau-enseignant")
public class EnseignantController {

    @Autowired private EnseignantAutorisationRepository autorisationRepository;
    @Autowired private MatiereRepository matiereRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String index(Model model) {
        Utilisateur currentUser = etablissementService.getCurrentUtilisateur();
        Long etabId = etablissementService.getCurrentEtablissementId();

        if (currentUser != null && "ENSEIGNANT".equals(currentUser.getRole())) {
            List<EnseignantAutorisation> auths = autorisationRepository
                .findByEnseignantIdAndEtablissementId(currentUser.getId(), etabId);

            // Maps pour les noms
            Map<Long, Matiere> matiereMap = matiereRepository.findByEtablissementIdOrderByNomAsc(etabId)
                .stream().collect(Collectors.toMap(Matiere::getId, m -> m));
            Map<Long, Classe> classeMap = classeRepository.findByEtablissementId(etabId)
                .stream().collect(Collectors.toMap(Classe::getId, c -> c));

            model.addAttribute("autorisations", auths);
            model.addAttribute("matiereMap", matiereMap);
            model.addAttribute("classeMap", classeMap);
            model.addAttribute("hasAutorisations", !auths.isEmpty());
        } else {
            model.addAttribute("autorisations", List.of());
            model.addAttribute("hasAutorisations", false);
        }

        return "tableau-enseignant";
    }
}
