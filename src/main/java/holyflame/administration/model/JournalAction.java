package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_actions")
public class JournalAction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String utilisateurEmail;
    private String utilisateurNom;
    private String action;         // ex: CREATION_ELEVE, PAIEMENT, MODIFICATION_NOTE …

    @Column(length = 500)
    private String detail;

    private String module;         // ELEVES, FINANCES, NOTES, ABSENCES, RH, PARAMETRES …
    private Long etablissementId;
    private LocalDateTime date;

    public JournalAction() {}

    public Long getId() { return id; }
    public String getUtilisateurEmail() { return utilisateurEmail; }
    public void setUtilisateurEmail(String utilisateurEmail) { this.utilisateurEmail = utilisateurEmail; }
    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public Long getEtablissementId() { return etablissementId; }
    public void setEtablissementId(Long etablissementId) { this.etablissementId = etablissementId; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
