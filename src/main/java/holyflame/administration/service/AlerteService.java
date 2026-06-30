package holyflame.administration.service;

import holyflame.administration.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AlerteService {

    @Autowired private EleveRepository eleveRepository;
    @Autowired private AbsenceRepository absenceRepository;
    @Autowired private PaiementRepository paiementRepository;
    @Autowired private PersonnelRepository personnelRepository;
    @Autowired private ParametreRepository parametreRepository;

    public record Alerte(String niveau, String icone, String message, String lien) {}

    public List<Alerte> getAlertes(Long etabId) {
        List<Alerte> alertes = new ArrayList<>();
        if (etabId == null) return alertes;

        // 1. Élèves avec ≥ 5 absences ce mois
        int moisActuel = LocalDate.now().getMonthValue();
        int anneeActuelle = LocalDate.now().getYear();
        long elevesAbsents = absenceRepository.findByEtablissementId(etabId).stream()
            .filter(a -> a.getDate() != null
                && a.getDate().getMonthValue() == moisActuel
                && a.getDate().getYear() == anneeActuelle)
            .collect(java.util.stream.Collectors.groupingBy(
                a -> a.getEleve() != null ? a.getEleve().getId() : -1L,
                java.util.stream.Collectors.counting()))
            .entrySet().stream()
            .filter(e -> e.getValue() >= 5)
            .count();

        if (elevesAbsents > 0) {
            alertes.add(new Alerte("danger", "bi-exclamation-triangle-fill",
                elevesAbsents + " élève(s) avec ≥ 5 absences ce mois", "/surveillance"));
        }

        // 2. Élèves sans aucun paiement ce trimestre (inscrits mais non à jour)
        long totalEleves = eleveRepository.countByEtablissementId(etabId);
        long elevesAvecPaiement = paiementRepository.findByEtablissementId(etabId).stream()
            .filter(p -> p.getDatePaiement() != null
                && p.getDatePaiement().getYear() == anneeActuelle)
            .map(p -> p.getEleve() != null ? p.getEleve().getId() : -1L)
            .distinct().count();
        long sansVersement = totalEleves - elevesAvecPaiement;
        if (sansVersement > 0) {
            alertes.add(new Alerte("warning", "bi-cash-coin",
                sansVersement + " élève(s) sans versement cette année", "/finances"));
        }

        // 3. Fin d'année scolaire dans moins de 30 jours
        parametreRepository.findByCleAndEtablissementId("T3_FIN", etabId).ifPresent(p -> {
            try {
                String[] parts = p.getValeur().split("/");
                LocalDate fin = LocalDate.of(Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
                long jours = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fin);
                if (jours >= 0 && jours <= 30) {
                    alertes.add(new Alerte("info", "bi-calendar-event",
                        "Fin d'année dans " + jours + " jour(s) — pensez aux bulletins", "/bulletins"));
                }
            } catch (Exception ignored) {}
        });

        // 4. Données vides (nouvel établissement)
        if (totalEleves == 0) {
            alertes.add(new Alerte("info", "bi-info-circle",
                "Aucun élève enregistré — commencez par le Secrétariat", "/secretariat"));
        }

        return alertes;
    }
}
