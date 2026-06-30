package holyflame.administration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parametres",
       uniqueConstraints = @UniqueConstraint(columnNames = {"cle", "etablissement_id"}))
public class Parametre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cle;

    @Column(nullable = false)
    private String valeur;

    private String description;
    private String categorie;

    @Column(name = "etablissement_id")
    private Long etablissementId;

    public Parametre() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCle() { return cle; }
    public void setCle(String cle) { this.cle = cle; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }
}
