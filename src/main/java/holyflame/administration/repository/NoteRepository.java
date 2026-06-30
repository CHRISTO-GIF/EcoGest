package holyflame.administration.repository;

import holyflame.administration.model.Eleve;
import holyflame.administration.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByEleveOrderByDateEvaluationDesc(Eleve eleve);
    List<Note> findByTrimestreOrderByDateEvaluationDesc(Integer trimestre);
    List<Note> findByEleveAndTrimestreOrderByMatiereNomAsc(Eleve eleve, Integer trimestre);
    List<Note> findAllByOrderByDateEvaluationDesc();

    // Pour le suivi : compter les notes saisies par matière + classe + trimestre
    @Query("SELECT COUNT(n) FROM Note n WHERE n.matiere.id = :matiereId AND n.eleve.classe.id = :classeId AND n.trimestre = :trimestre")
    long countByMatiereClasseTrimestre(@Param("matiereId") Long matiereId,
                                       @Param("classeId") Long classeId,
                                       @Param("trimestre") Integer trimestre);

    // Dernière saisie pour une combinaison matière + classe
    @Query("SELECT n FROM Note n WHERE n.matiere.id = :matiereId AND n.eleve.classe.id = :classeId ORDER BY n.saisieAt DESC")
    List<Note> findTopByMatiereAndClasse(@Param("matiereId") Long matiereId,
                                         @Param("classeId") Long classeId);

    // Notes saisies par un utilisateur
    List<Note> findBySaisieParIdOrderBySaisieAtDesc(Long saisieParId);

    @Query("SELECT n FROM Note n WHERE n.eleve.etablissementId = :etabId ORDER BY n.dateEvaluation DESC")
    List<Note> findByEtablissementId(@Param("etabId") Long etabId);

    @Query("SELECT COUNT(n) FROM Note n WHERE n.matiere.id = :id")
    long countByMatiereId(@Param("id") Long matiereId);

    @Modifying
    @Query("DELETE FROM Note n WHERE n.matiere.id = :id")
    void deleteByMatiereId(@Param("id") Long matiereId);

    @Modifying
    @Query("DELETE FROM Note n WHERE n.eleve.id = :id")
    void deleteByEleveId(@Param("id") Long eleveId);

    // Nombre d'élèves distincts ayant une note pour une matière+classe+type+trimestre donnés
    @Query("SELECT COUNT(DISTINCT n.eleve.id) FROM Note n " +
           "WHERE n.matiere.id = :matiereId AND n.eleve.classe.id = :classeId " +
           "AND n.type = :type AND n.trimestre = :trimestre")
    long countElevesByMatiereClasseTypeTrimestre(@Param("matiereId") Long matiereId,
                                                 @Param("classeId") Long classeId,
                                                 @Param("type") String type,
                                                 @Param("trimestre") Integer trimestre);
}
