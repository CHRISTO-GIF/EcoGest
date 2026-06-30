package holyflame.administration.repository;

import holyflame.administration.model.ModeleMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeleMessageRepository extends JpaRepository<ModeleMessage, Long> {
    List<ModeleMessage> findAllByOrderByTypeAscNomAsc();
}
