package holyflame.administration.repository;

import holyflame.administration.model.Programme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgrammeRepository extends JpaRepository<Programme, Long> {
    List<Programme> findAllByOrderByDateDebutDesc();
    List<Programme> findByStatutOrderByDateDebutDesc(String statut);
    void deleteByClasseId(Long classeId);
}
