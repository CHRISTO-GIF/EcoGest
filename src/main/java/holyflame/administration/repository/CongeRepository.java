package holyflame.administration.repository;

import holyflame.administration.model.Conge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CongeRepository extends JpaRepository<Conge, Long> {
    List<Conge> findAllByOrderByDateDebutDesc();
    List<Conge> findByStatut(String statut);
    List<Conge> findByPersonnelId(Long personnelId);
}
