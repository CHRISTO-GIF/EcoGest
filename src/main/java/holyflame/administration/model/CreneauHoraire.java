package holyflame.administration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "creneaux_horaires")
public class CreneauHoraire {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer jour;        // 1=Lundi … 6=Samedi
    private String heureDebut;   // ex: "07:30"
    private String heureFin;     // ex: "09:00"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id")
    private Classe classe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id")
    private Matiere matiere;

    private String enseignantNom;
    private String salle;
    private Long etablissementId;

    public CreneauHoraire() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getJour() { return jour; }
    public void setJour(Integer jour) { this.jour = jour; }
    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }
    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }
    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    public String getEnseignantNom() { return enseignantNom; }
    public void setEnseignantNom(String enseignantNom) { this.enseignantNom = enseignantNom; }
    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }
}
