package holyflame.administration.repository;

import holyflame.administration.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    @Query("SELECT a FROM Absence a WHERE a.eleve.etablissementId = :etabId")
    List<Absence> findByEtablissementId(@Param("etabId") Long etabId);

    @Query("SELECT COUNT(a) FROM Absence a WHERE a.eleve.etablissementId = :etabId")
    long countByEtablissementId(@Param("etabId") Long etabId);

    @Modifying
    @Query("DELETE FROM Absence a WHERE a.eleve.id = :id")
    void deleteByEleveId(@Param("id") Long eleveId);
}
