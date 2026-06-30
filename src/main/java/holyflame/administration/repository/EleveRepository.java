package holyflame.administration.repository;

import holyflame.administration.model.Eleve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EleveRepository extends JpaRepository<Eleve, Long> {
    List<Eleve> findByClasseIdOrderByNomAsc(Long classeId);
    List<Eleve> findAllByOrderByNomAscPrenomAsc();
    List<Eleve> findByEtablissementIdOrderByNomAscPrenomAsc(Long etablissementId);
    Optional<Eleve> findByCompteEmail(String compteEmail);
    Optional<Eleve> findByEmailParent(String emailParent);
    long countByEtablissementId(Long etablissementId);
    long countByClasseId(Long classeId);

    @Query("SELECT e FROM Eleve e WHERE e.etablissementId = :etabId AND " +
           "(LOWER(e.nom) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(e.matricule) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Eleve> searchByEtablissement(@Param("q") String q, @Param("etabId") Long etabId);

    @Modifying @Transactional
    @Query("UPDATE Eleve e SET e.etablissementId = :etabId WHERE e.etablissementId IS NULL")
    int migrateNullEtablissementId(@Param("etabId") Long etabId);
}
