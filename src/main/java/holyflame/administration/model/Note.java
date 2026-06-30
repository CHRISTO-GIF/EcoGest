package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    private Double valeur;
    private Double coefficient;
    private String type;       // DEVOIR, EXAMEN, PARTICIPATION
    private Integer trimestre; // 1, 2, 3
    private LocalDate dateEvaluation;
    private String commentaire;

    /** Horodatage automatique de la saisie en base */
    private LocalDateTime saisieAt;

    /** Identifiant du compte utilisateur qui a saisi la note */
    @Column(name = "saisie_par_id")
    private Long saisieParId;

    public Note() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Eleve getEleve() { return eleve; }
    public void setEleve(Eleve eleve) { this.eleve = eleve; }
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    public Double getValeur() { return valeur; }
    public void setValeur(Double valeur) { this.valeur = valeur; }
    public Double getCoefficient() { return coefficient; }
    public void setCoefficient(Double coefficient) { this.coefficient = coefficient; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getTrimestre() { return trimestre; }
    public void setTrimestre(Integer trimestre) { this.trimestre = trimestre; }
    public LocalDate getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDate dateEvaluation) { this.dateEvaluation = dateEvaluation; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDateTime getSaisieAt() { return saisieAt; }
    public void setSaisieAt(LocalDateTime saisieAt) { this.saisieAt = saisieAt; }
    public Long getSaisieParId() { return saisieParId; }
    public void setSaisieParId(Long saisieParId) { this.saisieParId = saisieParId; }
}
