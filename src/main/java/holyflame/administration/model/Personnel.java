package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personnels")
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String email;
    private String telephone;
    private String fonction; // ENSEIGNANT, DIRECTEUR, SECRETAIRE, SURVEILLANT, COMPTABLE, AUTRE
    private String matiereEnseignee;
    private String statut = "ACTIF"; // ACTIF, ARCHIVE
    private LocalDate dateEmbauche;
    private String adresse;

    @Column(length = 1000)
    private String remarques;

    private String codeAcces;
    private Long etablissementId;

    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentPersonnel> documents = new ArrayList<>();

    public Personnel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }
    public String getMatiereEnseignee() { return matiereEnseignee; }
    public void setMatiereEnseignee(String matiereEnseignee) { this.matiereEnseignee = matiereEnseignee; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getRemarques() { return remarques; }
    public void setRemarques(String remarques) { this.remarques = remarques; }
    public List<DocumentPersonnel> getDocuments() { return documents; }
    public void setDocuments(List<DocumentPersonnel> documents) { this.documents = documents; }
    public String getCodeAcces() { return codeAcces; }
    public void setCodeAcces(String codeAcces) { this.codeAcces = codeAcces; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }
}
