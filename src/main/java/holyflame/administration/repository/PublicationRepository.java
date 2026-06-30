package holyflame.administration.repository;

import holyflame.administration.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findByEtablissementIdOrderByDatePublicationDesc(Long etablissementId);

    // Pour le portail élève : publications pour toute l'école OU pour sa classe
    @Query("SELECT p FROM Publication p WHERE p.etablissementId = :etabId " +
           "AND (p.classeId IS NULL OR p.classeId = :classeId) " +
           "ORDER BY p.datePublication DESC")
    List<Publication> findForEleve(@Param("etabId") Long etabId, @Param("classeId") Long classeId);
}
