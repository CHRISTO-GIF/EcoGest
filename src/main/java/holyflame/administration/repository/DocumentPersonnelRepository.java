package holyflame.administration.repository;

import holyflame.administration.model.DocumentPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentPersonnelRepository extends JpaRepository<DocumentPersonnel, Long> {
    List<DocumentPersonnel> findByPersonnelIdOrderByDateUploadDesc(Long personnelId);
}
