package holyflame.administration.repository;

import holyflame.administration.model.MouvementInventaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MouvementInventaireRepository extends JpaRepository<MouvementInventaire, Long> {
    List<MouvementInventaire> findByArticleIdOrderByDateDesc(Long articleId);
}
