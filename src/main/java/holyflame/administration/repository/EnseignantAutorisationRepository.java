package holyflame.administration.repository;

import holyflame.administration.model.EnseignantAutorisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnseignantAutorisationRepository extends JpaRepository<EnseignantAutorisation, Long> {
    List<EnseignantAutorisation> findByEtablissementId(Long etablissementId);
    List<EnseignantAutorisation> findByEnseignantIdAndEtablissementId(Long enseignantId, Long etablissementId);
    boolean existsByEnseignantIdAndMatiereIdAndClasseId(Long enseignantId, Long matiereId, Long classeId);
    void deleteByEnseignantIdAndEtablissementId(Long enseignantId, Long etablissementId);
}
