package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles_inventaire")
public class ArticleInventaire {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String categorie;    // MOBILIER, INFORMATIQUE, LIVRE, SPORT, MATERIEL_BUREAU, AUTRE
    private int quantite;
    private String etat;         // NEUF, BON_ETAT, USE, EN_REPARATION, HORS_SERVICE
    private String localisation;
    private LocalDate dateAcquisition;
    private Double valeurUnitaire;
    private String fournisseur;
    private String numSerie;
    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MouvementInventaire> mouvements = new ArrayList<>();

    public ArticleInventaire() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }
    public Double getValeurUnitaire() { return valeurUnitaire; }
    public void setValeurUnitaire(Double valeurUnitaire) { this.valeurUnitaire = valeurUnitaire; }
    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }
    public String getNumSerie() { return numSerie; }
    public void setNumSerie(String numSerie) { this.numSerie = numSerie; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<MouvementInventaire> getMouvements() { return mouvements; }
    public void setMouvements(List<MouvementInventaire> mouvements) { this.mouvements = mouvements; }
}
