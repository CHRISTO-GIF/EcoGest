package holyflame.administration.repository;

import holyflame.administration.model.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface EtablissementRepository extends JpaRepository<Etablissement, Long> {
    Optional<Etablissement> findByCodeAcces(String codeAcces);
    List<Etablissement> findAllByOrderByNomAsc();
    List<Etablissement> findByStatut(String statut);
    List<Etablissement> findByStatutNotOrderByNomAsc(String statut);
    List<Etablissement> findByStatutOrderByNomAsc(String statut);
}
