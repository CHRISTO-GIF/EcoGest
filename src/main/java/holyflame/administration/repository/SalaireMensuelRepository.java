package holyflame.administration.repository;

import holyflame.administration.model.SalaireMensuel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaireMensuelRepository extends JpaRepository<SalaireMensuel, Long> {
    List<SalaireMensuel> findByAnneeOrderByMoisDescPersonnelNomAsc(int annee);
    List<SalaireMensuel> findByPersonnelIdOrderByAnneeDescMoisDesc(Long personnelId);
    List<SalaireMensuel> findByStatut(String statut);
}
