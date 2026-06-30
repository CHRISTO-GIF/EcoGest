package holyflame.administration.repository;

import holyflame.administration.model.CommunicationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunicationMessageRepository extends JpaRepository<CommunicationMessage, Long> {
    List<CommunicationMessage> findAllByOrderByDateEnvoiDesc();
    List<CommunicationMessage> findByStatutOrderByDateEnvoiDesc(String statut);
}
