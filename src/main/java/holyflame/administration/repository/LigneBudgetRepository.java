package holyflame.administration.repository;

import holyflame.administration.model.LigneBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LigneBudgetRepository extends JpaRepository<LigneBudget, Long> {
    List<LigneBudget> findByAnneeScolaireOrderByCategorieAscDesignationAsc(String annee);
    List<LigneBudget> findByCategorieAndAnneeScolaire(String categorie, String annee);

    List<LigneBudget> findByEtablissementIdAndAnneeScolaireOrderByCategorieAscDesignationAsc(Long etabId, String annee);
    List<LigneBudget> findByEtablissementIdOrderByCategorieAscDesignationAsc(Long etabId);

    @Modifying
    @Query("UPDATE LigneBudget l SET l.etablissementId = :etabId WHERE l.etablissementId IS NULL")
    int migrateNullEtablissementId(@Param("etabId") Long etabId);
}
