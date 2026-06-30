package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents_personnel")
public class DocumentPersonnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomOriginal;

    @Column(nullable = false)
    private String nomFichier;

    private String typeDocument; // DIPLOME, ACTE_NAISSANCE, CARTE_NNI, AUTRE

    @Column(nullable = false)
    private String cheminFichier;

    private String contentType;
    private Long taille;
    private LocalDateTime dateUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    public DocumentPersonnel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomOriginal() { return nomOriginal; }
    public void setNomOriginal(String nomOriginal) { this.nomOriginal = nomOriginal; }
    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public String getTypeDocument() { return typeDocument; }
    public void setTypeDocument(String typeDocument) { this.typeDocument = typeDocument; }
    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }
    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }
    public Personnel getPersonnel() { return personnel; }
    public void setPersonnel(Personnel personnel) { this.personnel = personnel; }
}
