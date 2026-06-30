package holyflame.administration.repository;

import holyflame.administration.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    @Query("SELECT p FROM Paiement p WHERE p.eleve.etablissementId = :etabId")
    List<Paiement> findByEtablissementId(@Param("etabId") Long etabId);

    @Modifying
    @Query("DELETE FROM Paiement p WHERE p.eleve.id = :id")
    void deleteByEleveId(@Param("id") Long eleveId);
}
