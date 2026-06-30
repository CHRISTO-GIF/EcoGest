package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archives_documents")
public class ArchiveDocument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 500)
    private String description;

    private String categorie;    // ELEVE, PERSONNEL, FINANCES, RH, ADMINISTRATIF, PEDAGOGIQUE
    private String tags;
    @Column(nullable = false)
    private String cheminFichier;
    private String nomOriginal;
    private String contentType;
    private Long taille;
    private String uploadePar;
    private LocalDateTime dateArchive;
    private boolean confidentiel;

    public ArchiveDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }
    public String getNomOriginal() { return nomOriginal; }
    public void setNomOriginal(String nomOriginal) { this.nomOriginal = nomOriginal; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }
    public String getUploadePar() { return uploadePar; }
    public void setUploadePar(String uploadePar) { this.uploadePar = uploadePar; }
    public LocalDateTime getDateArchive() { return dateArchive; }
    public void setDateArchive(LocalDateTime dateArchive) { this.dateArchive = dateArchive; }
    public boolean isConfidentiel() { return confidentiel; }
    public void setConfidentiel(boolean confidentiel) { this.confidentiel = confidentiel; }
}
