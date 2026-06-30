package holyflame.administration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "enseignant_autorisations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"enseignant_id","matiere_id","classe_id"}))
public class EnseignantAutorisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enseignant_id", nullable = false)
    private Long enseignantId;

    @Column(name = "matiere_id", nullable = false)
    private Long matiereId;

    @Column(name = "classe_id", nullable = false)
    private Long classeId;

    @Column(name = "etablissement_id")
    private Long etablissementId;

    public EnseignantAutorisation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEnseignantId() { return enseignantId; }
    public void setEnseignantId(Long enseignantId) { this.enseignantId = enseignantId; }
    public Long getMatiereId() { return matiereId; }
    public void setMatiereId(Long matiereId) { this.matiereId = matiereId; }
    public Long getClasseId() { return classeId; }
    public void setClasseId(Long classeId) { this.classeId = classeId; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }
}
