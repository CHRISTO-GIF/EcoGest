package holyflame.administration.controller;

import holyflame.administration.model.ArticleInventaire;
import holyflame.administration.model.MouvementInventaire;
import holyflame.administration.repository.ArticleInventaireRepository;
import holyflame.administration.repository.MouvementInventaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/inventaire")
public class InventaireController {

    @Autowired private ArticleInventaireRepository articleRepository;
    @Autowired private MouvementInventaireRepository mouvementRepository;

    @GetMapping
    public String index(Model model) {
        List<ArticleInventaire> articles = articleRepository.findAllByOrderByCategorieAscNomAsc();
        model.addAttribute("articles", articles);
        model.addAttribute("totalArticles", articles.size());
        model.addAttribute("totalNeuf",      articleRepository.countByEtat("NEUF"));
        model.addAttribute("totalReparation",articleRepository.countByEtat("EN_REPARATION"));
        model.addAttribute("totalHorsSvc",   articleRepository.countByEtat("HORS_SERVICE"));
        double valeurTotale = articles.stream()
            .mapToDouble(a -> (a.getValeurUnitaire() != null ? a.getValeurUnitaire() : 0) * a.getQuantite()).sum();
        model.addAttribute("valeurTotale", valeurTotale);
        return "inventaire";
    }

    @PostMapping
    public String ajouter(
            @RequestParam String nom,
            @RequestParam String categorie,
            @RequestParam int quantite,
            @RequestParam String etat,
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAcquisition,
            @RequestParam(required = false) Double valeurUnitaire,
            @RequestParam(required = false) String fournisseur,
            @RequestParam(required = false) String numSerie,
            @RequestParam(required = false) String notes) {

        ArticleInventaire a = new ArticleInventaire();
        a.setNom(nom); a.setCategorie(categorie); a.setQuantite(quantite); a.setEtat(etat);
        a.setLocalisation(localisation); a.setDateAcquisition(dateAcquisition);
        a.setValeurUnitaire(valeurUnitaire); a.setFournisseur(fournisseur);
        a.setNumSerie(numSerie); a.setNotes(notes);
        articleRepository.save(a);
        return "redirect:/inventaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam String nom, @RequestParam String categorie,
            @RequestParam int quantite, @RequestParam String etat,
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAcquisition,
            @RequestParam(required = false) Double valeurUnitaire,
            @RequestParam(required = false) String fournisseur,
            @RequestParam(required = false) String numSerie,
            @RequestParam(required = false) String notes) {

        ArticleInventaire a = articleRepository.findById(id).orElseThrow();
        a.setNom(nom); a.setCategorie(categorie); a.setQuantite(quantite); a.setEtat(etat);
        a.setLocalisation(localisation); a.setDateAcquisition(dateAcquisition);
        a.setValeurUnitaire(valeurUnitaire); a.setFournisseur(fournisseur);
        a.setNumSerie(numSerie); a.setNotes(notes);
        articleRepository.save(a);
        return "redirect:/inventaire";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        articleRepository.deleteById(id);
        return "redirect:/inventaire";
    }

    @PostMapping("/{id}/mouvement")
    public String ajouterMouvement(
            @PathVariable Long id,
            @RequestParam String type,
            @RequestParam int quantite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String motif,
            @RequestParam(required = false) String effectuePar) {

        ArticleInventaire a = articleRepository.findById(id).orElseThrow();
        MouvementInventaire m = new MouvementInventaire();
        m.setArticle(a); m.setType(type); m.setQuantite(quantite);
        m.setDate(date != null ? date : LocalDate.now());
        m.setMotif(motif); m.setEffectuePar(effectuePar);

        if ("ENTREE".equals(type)) a.setQuantite(a.getQuantite() + quantite);
        else if ("SORTIE".equals(type)) a.setQuantite(Math.max(0, a.getQuantite() - quantite));
        else if ("REPARATION".equals(type)) a.setEtat("EN_REPARATION");

        mouvementRepository.save(m);
        articleRepository.save(a);
        return "redirect:/inventaire";
    }
}
