package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "etablissements")
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String ville;
    private String adresse;
    private String telephone;
    private String email;

    @Column(unique = true, nullable = false)
    private String codeAcces; // code unique d'accès (ex: HF-2025-001)

    private String statut = "ACTIF"; // ACTIF, SUSPENDU
    private LocalDate dateCreation;
    private String anneeScolaire;
    private String contact;      // nom du directeur
    private String typeEtablissement; // COLLEGE, PRIMAIRE, LYCEE...
    private String monnaie = "FCFA";
    private String planAbonnement = "GRATUIT"; // GRATUIT, STANDARD, PREMIUM
    private String pays;
    private String province;
    private String typeGestion; // PRIVE, PUBLIC
    private String telephoneAdmin;
    private String indicatifPays;

    public Etablissement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCodeAcces() { return codeAcces; }
    public void setCodeAcces(String codeAcces) { this.codeAcces = codeAcces; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getTypeEtablissement() { return typeEtablissement; }
    public void setTypeEtablissement(String typeEtablissement) { this.typeEtablissement = typeEtablissement; }
    public String getMonnaie() { return monnaie; }
    public void setMonnaie(String monnaie) { this.monnaie = monnaie; }
    public String getPlanAbonnement() { return planAbonnement; }
    public void setPlanAbonnement(String planAbonnement) { this.planAbonnement = planAbonnement; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getTypeGestion() { return typeGestion; }
    public void setTypeGestion(String typeGestion) { this.typeGestion = typeGestion; }
    public String getTelephoneAdmin() { return telephoneAdmin; }
    public void setTelephoneAdmin(String telephoneAdmin) { this.telephoneAdmin = telephoneAdmin; }
    public String getIndicatifPays() { return indicatifPays; }
    public void setIndicatifPays(String indicatifPays) { this.indicatifPays = indicatifPays; }
}
