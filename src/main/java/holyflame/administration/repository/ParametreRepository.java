package holyflame.administration.repository;

import holyflame.administration.model.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    List<Parametre> findByEtablissementId(Long etablissementId);
    Optional<Parametre> findByCleAndEtablissementId(String cle, Long etablissementId);
    boolean existsByCleAndEtablissementId(String cle, Long etablissementId);
    List<Parametre> findByCategorieAndEtablissementIdOrderByCleAsc(String categorie, Long etablissementId);
}
