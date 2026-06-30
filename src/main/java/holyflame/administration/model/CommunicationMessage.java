package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "communications")
public class CommunicationMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;         // EMAIL, SMS, INTERNE
    private String sujet;
    @Column(length = 3000)
    private String contenu;
    @Column(length = 2000)
    private String destinataires;
    private String cibleType;    // TOUS_PARENTS, CLASSE, ELEVE, PERSONNEL
    private String statut;       // BROUILLON, ENVOYE, ECHEC
    private LocalDateTime dateEnvoi;
    private String expediteur;
    private int nbDestinataires;

    public CommunicationMessage() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getDestinataires() { return destinataires; }
    public void setDestinataires(String destinataires) { this.destinataires = destinataires; }
    public String getCibleType() { return cibleType; }
    public void setCibleType(String cibleType) { this.cibleType = cibleType; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public String getExpediteur() { return expediteur; }
    public void setExpediteur(String expediteur) { this.expediteur = expediteur; }
    public int getNbDestinataires() { return nbDestinataires; }
    public void setNbDestinataires(int nbDestinataires) { this.nbDestinataires = nbDestinataires; }
}
