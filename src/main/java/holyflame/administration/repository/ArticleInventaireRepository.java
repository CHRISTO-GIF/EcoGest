package holyflame.administration.repository;

import holyflame.administration.model.ArticleInventaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleInventaireRepository extends JpaRepository<ArticleInventaire, Long> {
    List<ArticleInventaire> findAllByOrderByCategorieAscNomAsc();
    List<ArticleInventaire> findByEtat(String etat);
    long countByEtat(String etat);
}
