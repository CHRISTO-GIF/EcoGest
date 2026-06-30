package holyflame.administration.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eleves")
public class Eleve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String matricule;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String telephoneParent;
    private String emailParent;
    private String adresse;
    private String statutInscription;
    private String compteEmail;
    private Long etablissementId;
    private String photoPath;
    private String documentsPaths;
    private boolean carteImprimee = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    public Eleve() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephoneParent() {
        return telephoneParent;
    }

    public void setTelephoneParent(String telephoneParent) {
        this.telephoneParent = telephoneParent;
    }

    public String getEmailParent() {
        return emailParent;
    }

    public void setEmailParent(String emailParent) {
        this.emailParent = emailParent;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getStatutInscription() {
        return statutInscription;
    }

    public void setStatutInscription(String statutInscription) {
        this.statutInscription = statutInscription;
    }

    public String getCompteEmail() { return compteEmail; }
    public void setCompteEmail(String compteEmail) { this.compteEmail = compteEmail; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public String getDocumentsPaths() { return documentsPaths; }
    public void setDocumentsPaths(String documentsPaths) { this.documentsPaths = documentsPaths; }
    public boolean isCarteImprimee() { return carteImprimee; }
    public void setCarteImprimee(boolean carteImprimee) { this.carteImprimee = carteImprimee; }
}
