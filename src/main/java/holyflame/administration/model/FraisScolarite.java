package holyflame.administration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "frais_scolarite")
public class FraisScolarite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String designation;

    private String typeFrais;     // INSCRIPTION, SCOLARITE, CANTINE, TRANSPORT, AUTRE
    private Double montant;
    private String echeance;      // ANNUEL, T1, T2, T3, MENSUEL
    private String niveauCible;   // null = tous niveaux
    private boolean obligatoire;
    private Long etablissementId;

    public FraisScolarite() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getTypeFrais() { return typeFrais; }
    public void setTypeFrais(String typeFrais) { this.typeFrais = typeFrais; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public String getEcheance() { return echeance; }
    public void setEcheance(String echeance) { this.echeance = echeance; }
    public String getNiveauCible() { return niveauCible; }
    public void setNiveauCible(String niveauCible) { this.niveauCible = niveauCible; }
    public boolean isObligatoire() { return obligatoire; }
    public void setObligatoire(boolean obligatoire) { this.obligatoire = obligatoire; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }
}
