package holyflame.administration.repository;

import holyflame.administration.model.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    List<Contrat> findAllByOrderByDateDebutDesc();
    List<Contrat> findByStatut(String statut);
    List<Contrat> findByPersonnelId(Long personnelId);
}
