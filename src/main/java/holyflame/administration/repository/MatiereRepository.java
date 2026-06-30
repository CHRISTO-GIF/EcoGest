package holyflame.administration.repository;

import holyflame.administration.model.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    List<Matiere> findAllByOrderByNomAsc();
    List<Matiere> findByEtablissementIdOrderByNomAsc(Long etablissementId);

    @Query("SELECT m FROM Matiere m WHERE m.etablissementId = :etabId OR m.etablissementId IS NULL ORDER BY m.nom ASC")
    List<Matiere> findByEtabIdOrNull(@Param("etabId") Long etabId);

    @Modifying @Transactional
    @Query("UPDATE Matiere m SET m.etablissementId = :etabId WHERE m.etablissementId IS NULL")
    int migrateNullEtablissementId(@Param("etabId") Long etabId);
}
