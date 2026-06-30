package holyflame.administration.repository;

import holyflame.administration.model.ArchiveDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchiveDocumentRepository extends JpaRepository<ArchiveDocument, Long> {
    List<ArchiveDocument> findAllByOrderByDateArchiveDesc();
    List<ArchiveDocument> findByCategorieOrderByDateArchiveDesc(String categorie);
    List<ArchiveDocument> findByNomContainingIgnoreCaseOrTagsContainingIgnoreCase(String nom, String tags);
}
