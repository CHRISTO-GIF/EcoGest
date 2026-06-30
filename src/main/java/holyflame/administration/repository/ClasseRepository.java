package holyflame.administration.repository;

import holyflame.administration.model.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClasseRepository extends JpaRepository<Classe, Long> {
    boolean existsByNomAndAnneeScolaire(String nom, String anneeScolaire);
    boolean existsByNomAndAnneeScolaireAndEtablissementId(String nom, String anneeScolaire, Long etablissementId);
    List<Classe> findByAnneeScolaire(String anneeScolaire);
    List<Classe> findByEtablissementId(Long etablissementId);
    List<Classe> findByAnneeScolaireAndEtablissementId(String anneeScolaire, Long etablissementId);

    @Query("SELECT c FROM Classe c WHERE c.etablissementId = :etabId OR c.etablissementId IS NULL ORDER BY c.nom ASC")
    List<Classe> findByEtabIdOrNull(@Param("etabId") Long etabId);

    @Modifying @Transactional
    @Query("UPDATE Classe c SET c.etablissementId = :etabId WHERE c.etablissementId IS NULL")
    int migrateNullEtablissementId(@Param("etabId") Long etabId);
}
