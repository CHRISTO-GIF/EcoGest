package holyflame.administration.repository;

import holyflame.administration.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRoleAndEtablissementIdOrderByNomAsc(String role, Long etablissementId);
    List<Utilisateur> findByEtablissementId(Long etablissementId);

    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.etablissement ORDER BY COALESCE(u.nom,'') ASC, COALESCE(u.prenom,'') ASC")
    List<Utilisateur> findAllWithEtablissement();
}
