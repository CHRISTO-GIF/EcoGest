package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "salaires_mensuels")
public class SalaireMensuel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    private int mois;
    private int annee;
    private Double salaireBase;
    private Double primes;
    private Double retenues;
    private Double net;
    private String statut;      // EN_ATTENTE, PAYE
    private LocalDate datePaiement;

    public SalaireMensuel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Personnel getPersonnel() { return personnel; }
    public void setPersonnel(Personnel personnel) { this.personnel = personnel; }
    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public Double getSalaireBase() { return salaireBase; }
    public void setSalaireBase(Double salaireBase) { this.salaireBase = salaireBase; }
    public Double getPrimes() { return primes; }
    public void setPrimes(Double primes) { this.primes = primes; }
    public Double getRetenues() { return retenues; }
    public void setRetenues(Double retenues) { this.retenues = retenues; }
    public Double getNet() { return net; }
    public void setNet(Double net) { this.net = net; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }
}
