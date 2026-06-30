package holyflame.administration.repository;

import holyflame.administration.model.CreneauHoraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreneauHoraireRepository extends JpaRepository<CreneauHoraire, Long> {
    List<CreneauHoraire> findByEtablissementIdAndClasseIdOrderByJourAscHeureDebutAsc(Long etabId, Long classeId);
    List<CreneauHoraire> findByEtablissementIdOrderByJourAscHeureDebutAsc(Long etabId);
    void deleteByEtablissementId(Long etabId);
    void deleteByClasseId(Long classeId);

    @Modifying
    @Query("DELETE FROM CreneauHoraire c WHERE c.matiere.id = :id")
    void deleteByMatiereId(@Param("id") Long matiereId);
}
