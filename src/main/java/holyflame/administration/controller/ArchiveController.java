package holyflame.administration.controller;

import holyflame.administration.model.ArchiveDocument;
import holyflame.administration.repository.ArchiveDocumentRepository;
import holyflame.administration.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/archives")
public class ArchiveController {

    @Autowired private ArchiveDocumentRepository archiveRepository;
    @Autowired private FileStorageService fileStorageService;

    @GetMapping
    public String index(@RequestParam(required = false) String categorie,
                        @RequestParam(required = false) String q,
                        Model model) {
        List<ArchiveDocument> docs;
        if (q != null && !q.isBlank()) {
            docs = archiveRepository.findByNomContainingIgnoreCaseOrTagsContainingIgnoreCase(q, q);
        } else if (categorie != null && !categorie.isBlank()) {
            docs = archiveRepository.findByCategorieOrderByDateArchiveDesc(categorie);
        } else {
            docs = archiveRepository.findAllByOrderByDateArchiveDesc();
        }
        model.addAttribute("docs", docs);
        model.addAttribute("categorieFiltre", categorie);
        model.addAttribute("q", q);
        model.addAttribute("totalDocs", archiveRepository.count());
        return "archives";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String archiver(
            @RequestParam String nom,
            @RequestParam String categorie,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "false") boolean confidentiel,
            @RequestParam("fichier") MultipartFile fichier,
            Authentication auth) {

        if (fichier.isEmpty()) return "redirect:/archives";
        try {
            String path = fileStorageService.store(fichier, "archives/" + categorie.toLowerCase());
            ArchiveDocument doc = new ArchiveDocument();
            doc.setNom(nom); doc.setCategorie(categorie); doc.setDescription(description);
            doc.setTags(tags); doc.setConfidentiel(confidentiel);
            doc.setCheminFichier(path); doc.setNomOriginal(fichier.getOriginalFilename());
            doc.setContentType(fichier.getContentType()); doc.setTaille(fichier.getSize());
            doc.setUploadePar(auth != null ? auth.getName() : "système");
            doc.setDateArchive(LocalDateTime.now());
            archiveRepository.save(doc);
        } catch (IOException e) {
            return "redirect:/archives?error=upload";
        }
        return "redirect:/archives?saved=true";
    }

    @GetMapping("/{id}/telecharger")
    public ResponseEntity<Resource> telecharger(@PathVariable Long id) throws IOException {
        ArchiveDocument doc = archiveRepository.findById(id).orElseThrow();
        Resource resource = fileStorageService.loadAsResource(doc.getCheminFichier());
        String ct = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(ct))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomOriginal() + "\"")
            .body(resource);
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        archiveRepository.findById(id).ifPresent(doc -> {
            fileStorageService.delete(doc.getCheminFichier());
            archiveRepository.delete(doc);
        });
        return "redirect:/archives";
    }
}
