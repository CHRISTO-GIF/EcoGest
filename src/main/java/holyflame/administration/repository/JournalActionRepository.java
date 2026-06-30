package holyflame.administration.repository;

import holyflame.administration.model.JournalAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface JournalActionRepository extends JpaRepository<JournalAction, Long> {
    List<JournalAction> findByEtablissementIdOrderByDateDesc(Long etablissementId, PageRequest pr);
    List<JournalAction> findByEtablissementIdAndModuleOrderByDateDesc(Long etablissementId, String module, PageRequest pr);
    void deleteByEtablissementId(Long etablissementId);
}
