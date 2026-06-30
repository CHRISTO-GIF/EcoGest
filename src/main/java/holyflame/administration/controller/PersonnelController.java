package holyflame.administration.controller;

import holyflame.administration.model.DocumentPersonnel;
import holyflame.administration.model.Personnel;
import holyflame.administration.repository.*;
import holyflame.administration.service.EtablissementService;
import holyflame.administration.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/personnel")
public class PersonnelController {

    @Autowired private PersonnelRepository personnelRepository;
    @Autowired private DocumentPersonnelRepository documentRepository;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private ContratRepository contratRepository;
    @Autowired private CongeRepository congeRepository;
    @Autowired private SalaireMensuelRepository salaireRepository;
    @Autowired private EtablissementService etablissementService;

    @GetMapping
    public String liste(Model model) {
        Long etabId = etablissementService.getCurrentEtablissementId();

        // Migration automatique : associer les anciens personnels sans etabId
        if (etabId != null) {
            int migrated = personnelRepository.migrateNullEtablissementId(etabId);
            if (migrated > 0) {
                model.addAttribute("migrationMsg", migrated + " fiche(s) existante(s) associée(s) à votre établissement.");
            }
        }

        List<Personnel> personnels = etabId != null
            ? personnelRepository.findByEtablissementIdOrderByNomAscPrenomAsc(etabId)
            : personnelRepository.findAllByOrderByNomAscPrenomAsc();

        model.addAttribute("personnels", personnels);
        model.addAttribute("totalPersonnels",  personnels.size());
        model.addAttribute("totalActifs", personnels.stream().filter(p -> "ACTIF".equals(p.getStatut())).count());
        model.addAttribute("totalEnseignants", personnels.stream().filter(p -> "ENSEIGNANT".equals(p.getFonction())).count());

        return "personnel";
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        Personnel p = personnelRepository.findById(id).orElseThrow();
        List<DocumentPersonnel> docs = documentRepository.findByPersonnelIdOrderByDateUploadDesc(id);
        Map<String, List<DocumentPersonnel>> docsByType = docs.stream()
            .collect(Collectors.groupingBy(d -> d.getTypeDocument() != null ? d.getTypeDocument() : "AUTRE"));

        model.addAttribute("personnel",  p);
        model.addAttribute("documents",  docs);
        model.addAttribute("docsByType", docsByType);
        model.addAttribute("contrats",   contratRepository.findByPersonnelId(id));
        model.addAttribute("conges",     congeRepository.findByPersonnelId(id));
        model.addAttribute("salaires",   salaireRepository.findByPersonnelIdOrderByAnneeDescMoisDesc(id));
        return "personnel-fiche";
    }

    @GetMapping("/nouveau")
    public String nouveauForm() {
        return "redirect:/personnel?showForm=true";
    }

    @PostMapping
    public String ajouter(
            @RequestParam String nom, @RequestParam String prenom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone,
            @RequestParam String fonction,
            @RequestParam(required = false) String matiereEnseignee,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEmbauche,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String remarques,
            RedirectAttributes ra) {

        Personnel p = new Personnel();
        p.setNom(nom.toUpperCase().trim());
        p.setPrenom(prenom.trim());
        p.setEmail(email != null && !email.isBlank() ? email.trim() : null);
        p.setTelephone(telephone);
        p.setFonction(fonction);
        p.setMatiereEnseignee(matiereEnseignee);
        p.setStatut(statut != null && !statut.isBlank() ? statut : "ACTIF");
        p.setDateEmbauche(dateEmbauche);
        p.setAdresse(adresse);
        p.setRemarques(remarques);
        p.setEtablissementId(etablissementService.getCurrentEtablissementId());
        personnelRepository.save(p);

        ra.addFlashAttribute("successMsg",
            p.getNom() + " " + p.getPrenom() + " a été ajouté(e) avec succès.");
        return "redirect:/personnel";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam String nom, @RequestParam String prenom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone,
            @RequestParam String fonction,
            @RequestParam(required = false) String matiereEnseignee,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEmbauche,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String remarques,
            RedirectAttributes ra) {

        Personnel p = personnelRepository.findById(id).orElseThrow();
        p.setNom(nom.toUpperCase().trim());
        p.setPrenom(prenom.trim());
        p.setEmail(email != null && !email.isBlank() ? email.trim() : null);
        p.setTelephone(telephone);
        p.setFonction(fonction);
        p.setMatiereEnseignee(matiereEnseignee);
        if (statut != null && !statut.isBlank()) p.setStatut(statut);
        p.setDateEmbauche(dateEmbauche);
        p.setAdresse(adresse);
        p.setRemarques(remarques);
        // Préserver ou affecter l'etabId si absent
        if (p.getEtablissementId() == null) {
            p.setEtablissementId(etablissementService.getCurrentEtablissementId());
        }
        personnelRepository.save(p);

        ra.addFlashAttribute("successMsg", p.getNom() + " " + p.getPrenom() + " mis(e) à jour.");
        return "redirect:/personnel";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        Personnel p = personnelRepository.findById(id).orElseThrow();
        String nom = p.getNom() + " " + p.getPrenom();
        documentRepository.findByPersonnelIdOrderByDateUploadDesc(id)
            .forEach(d -> fileStorageService.delete(d.getCheminFichier()));
        personnelRepository.delete(p);
        ra.addFlashAttribute("infoMsg", nom + " supprimé(e).");
        return "redirect:/personnel";
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadDocument(
            @PathVariable Long id,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("typeDocument") String typeDocument) {

        if (fichier.isEmpty()) return "redirect:/personnel/" + id + "#documents";
        Personnel p = personnelRepository.findById(id).orElseThrow();
        try {
            String path = fileStorageService.store(fichier, "personnel/" + id);
            DocumentPersonnel doc = new DocumentPersonnel();
            doc.setPersonnel(p);
            doc.setNomOriginal(fichier.getOriginalFilename());
            doc.setNomFichier(Paths.get(path).getFileName().toString());
            doc.setCheminFichier(path);
            doc.setTypeDocument(typeDocument);
            doc.setContentType(fichier.getContentType());
            doc.setTaille(fichier.getSize());
            doc.setDateUpload(LocalDateTime.now());
            documentRepository.save(doc);
        } catch (IOException e) {
            return "redirect:/personnel/" + id + "?error=upload#documents";
        }
        return "redirect:/personnel/" + id + "?uploaded=true#documents";
    }

    @GetMapping("/{id}/documents/{docId}/telecharger")
    public ResponseEntity<Resource> telecharger(@PathVariable Long id, @PathVariable Long docId) throws IOException {
        DocumentPersonnel doc = documentRepository.findById(docId).orElseThrow();
        Resource resource = fileStorageService.loadAsResource(doc.getCheminFichier());
        String ct = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(ct))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomOriginal() + "\"")
            .body(resource);
    }

    @PostMapping("/{id}/documents/{docId}/supprimer")
    public String supprimerDocument(@PathVariable Long id, @PathVariable Long docId) {
        DocumentPersonnel doc = documentRepository.findById(docId).orElseThrow();
        fileStorageService.delete(doc.getCheminFichier());
        documentRepository.delete(doc);
        return "redirect:/personnel/" + id + "#documents";
    }
}
