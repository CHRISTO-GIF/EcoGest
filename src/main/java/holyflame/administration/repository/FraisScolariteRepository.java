package holyflame.administration.repository;

import holyflame.administration.model.FraisScolarite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraisScolariteRepository extends JpaRepository<FraisScolarite, Long> {
    List<FraisScolarite> findAllByOrderByTypeFraisAscDesignationAsc();
    List<FraisScolarite> findByEtablissementIdOrderByTypeFraisAscDesignationAsc(Long etablissementId);
}
