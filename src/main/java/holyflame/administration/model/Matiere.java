package holyflame.administration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "matieres")
public class Matiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private Double coefficient;
    private String description;
    private Long etablissementId;

    public Matiere() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Double getCoefficient() { return coefficient; }
    public void setCoefficient(Double coefficient) { this.coefficient = coefficient; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }
}
