package holyflame.administration.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mouvements_inventaire")
public class MouvementInventaire {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleInventaire article;

    private String type;         // ENTREE, SORTIE, REPARATION
    private int quantite;
    private LocalDate date;
    private String motif;
    private String effectuePar;

    public MouvementInventaire() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ArticleInventaire getArticle() { return article; }
    public void setArticle(ArticleInventaire article) { this.article = article; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getEffectuePar() { return effectuePar; }
    public void setEffectuePar(String effectuePar) { this.effectuePar = effectuePar; }
}
