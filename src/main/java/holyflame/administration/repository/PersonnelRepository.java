package holyflame.administration.repository;

import holyflame.administration.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    List<Personnel> findAllByOrderByNomAscPrenomAsc();
    List<Personnel> findByEtablissementIdOrderByNomAscPrenomAsc(Long etablissementId);
    List<Personnel> findByFonctionAndEtablissementIdOrderByNomAsc(String fonction, Long etablissementId);
    Optional<Personnel> findByEmailAndEtablissementId(String email, Long etablissementId);

    // Inclut les anciens enregistrements sans etablissementId (migration)
    @Query("SELECT p FROM Personnel p WHERE p.etablissementId = :etabId OR p.etablissementId IS NULL ORDER BY p.nom ASC, p.prenom ASC")
    List<Personnel> findByEtablissementIdOrNullOrderByNomAsc(@Param("etabId") Long etabId);

    // Migration : associer les personnels sans etabId à un etablissement
    @Modifying
    @Transactional
    @Query("UPDATE Personnel p SET p.etablissementId = :etabId WHERE p.etablissementId IS NULL")
    int migrateNullEtablissementId(@Param("etabId") Long etabId);

    long countByStatut(String statut);
    long countByFonction(String fonction);
    long countByStatutAndEtablissementId(String statut, Long etablissementId);
    long countByFonctionAndEtablissementId(String fonction, Long etablissementId);
    long countByEtablissementId(Long etablissementId);

    @Query("SELECT p FROM Personnel p WHERE p.etablissementId = :etabId AND " +
           "(LOWER(p.nom) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.prenom) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(p.fonction) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Personnel> searchByEtablissement(@Param("q") String q, @Param("etabId") Long etabId);
}
