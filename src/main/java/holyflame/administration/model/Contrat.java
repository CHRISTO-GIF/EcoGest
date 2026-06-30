package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contrats")
public class Contrat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    private String typeContrat;  // CDI, CDD, VACATAIRE, STAGE
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double salaireBase;
    private String statut;       // ACTIF, EXPIRE, RESILIE
    @Column(length = 1000)
    private String notes;

    public Contrat() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Personnel getPersonnel() { return personnel; }
    public void setPersonnel(Personnel personnel) { this.personnel = personnel; }
    public String getTypeContrat() { return typeContrat; }
    public void setTypeContrat(String typeContrat) { this.typeContrat = typeContrat; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public Double getSalaireBase() { return salaireBase; }
    public void setSalaireBase(Double salaireBase) { this.salaireBase = salaireBase; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
