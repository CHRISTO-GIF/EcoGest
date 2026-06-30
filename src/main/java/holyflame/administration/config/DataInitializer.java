package holyflame.administration.config;

import holyflame.administration.model.*;
import holyflame.administration.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private EtablissementRepository etablissementRepository;
    @Autowired private ClasseRepository classeRepository;
    @Autowired private MatiereRepository matiereRepository;
    @Autowired private EleveRepository eleveRepository;
    @Autowired private ParametreRepository parametreRepository;
    @Autowired private FraisScolariteRepository fraisRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 1. SUPER_ADMIN (aucun établissement)
        if (utilisateurRepository.findByEmail("superadmin@holyflame.com").isEmpty()) {
            saveUser("Super", "Admin", "superadmin@holyflame.com", "super123", "SUPER_ADMIN", null);
        }

        // 2. Établissement par défaut
        Etablissement defEtab = etablissementRepository.findByCodeAcces("HF-DEMO-001")
            .orElseGet(() -> {
                Etablissement e = new Etablissement();
                e.setNom("HolyFlame");
                e.setVille("Abidjan");
                e.setCodeAcces("HF-DEMO-001");
                e.setStatut("ACTIF");
                e.setDateCreation(LocalDate.now());
                e.setAnneeScolaire("2025-2026");
                e.setTypeEtablissement("COLLEGE");
                e.setMonnaie("FCFA");
                return etablissementRepository.save(e);
            });
        Long etabId = defEtab.getId();

        // 3. Utilisateurs démo liés à l'établissement par défaut (chaque vérification est indépendante)
        if (utilisateurRepository.findByEmail("admin@holyflame.com").isEmpty())
            saveUser("Admin",   "HolyFlame", "admin@holyflame.com",      "admin123",  "ADMIN",      defEtab);
        if (utilisateurRepository.findByEmail("secretaire@holyflame.com").isEmpty())
            saveUser("Dupont",  "Marie",     "secretaire@holyflame.com", "secret123", "SECRETAIRE", defEtab);
        if (utilisateurRepository.findByEmail("tresorier@holyflame.com").isEmpty())
            saveUser("Martin",  "Paul",      "tresorier@holyflame.com",  "tresor123", "TRESORIER",  defEtab);
        if (utilisateurRepository.findByEmail("enseignant@holyflame.com").isEmpty())
            saveUser("Leclerc", "Jean",      "enseignant@holyflame.com", "ens123",    "ENSEIGNANT", defEtab);
        if (utilisateurRepository.findByEmail("eleve@holyflame.com").isEmpty())
            saveUser("KONAN",   "Amara",     "eleve@holyflame.com",      "eleve123",  "ELEVE",      defEtab);

        // 4. Classes
        if (classeRepository.count() == 0) {
            saveClasse("6ème A", "6ème", "2025-2026", etabId);
            saveClasse("5ème A", "5ème", "2025-2026", etabId);
            saveClasse("4ème A", "4ème", "2025-2026", etabId);
            saveClasse("3ème A", "3ème", "2025-2026", etabId);
            saveClasse("2nde A", "2nde", "2025-2026", etabId);
        }

        // 5. Matières
        if (matiereRepository.count() == 0) {
            saveMatiere("Mathématiques", 4.0, "Algèbre, géométrie, statistiques",     etabId);
            saveMatiere("Français",      4.0, "Littérature, grammaire, expression",   etabId);
            saveMatiere("Sciences",      3.0, "SVT, physique-chimie",                 etabId);
            saveMatiere("Histoire-Géo",  3.0, "Histoire et géographie",               etabId);
            saveMatiere("Anglais",       3.0, "Langue vivante 1",                     etabId);
            saveMatiere("EPS",           2.0, "Éducation physique et sportive",       etabId);
            saveMatiere("Informatique",  2.0, "Algorithmique, bureautique",           etabId);
        }

        // 6. Élèves
        if (eleveRepository.count() == 0) {
            Classe c6A = classeRepository.findAll().get(0);
            Classe c5A = classeRepository.findAll().get(1);
            saveEleve("HF-2026-001","KONAN",    "Amara",   LocalDate.of(2012,3,15),"0701234567","konan@email.com",    c6A, etabId);
            saveEleve("HF-2026-002","DIALLO",   "Fatima",  LocalDate.of(2012,7,22),"0702345678","diallo@email.com",   c6A, etabId);
            saveEleve("HF-2026-003","COULIBALY","Ibrahim", LocalDate.of(2011,11,5),"0703456789","coulibaly@email.com",c5A, etabId);
            saveEleve("HF-2026-004","TRAORE",   "Mariama", LocalDate.of(2011,4,18),"0704567890","traore@email.com",   c5A, etabId);
            saveEleve("HF-2026-005","BAMBA",    "Ousmane", LocalDate.of(2012,9,30),"0705678901","bamba@email.com",    c6A, etabId);
        }

        // 7. Paramètres de base
        if (parametreRepository.count() == 0) {
            saveParam("ANNEE_SCOLAIRE",   "2025-2026",   "Année scolaire en cours",         "GENERAL",     etabId);
            saveParam("TRIMESTRE_ACTUEL", "1",           "Trimestre actuel",                "GENERAL",     etabId);
            saveParam("NOM_ETABLISSEMENT","HolyFlame",   "Nom de l'établissement",          "GENERAL",     etabId);
            saveParam("VILLE",            "Abidjan",     "Ville de l'établissement",        "GENERAL",     etabId);
            saveParam("T1_DEBUT",         "01/10/2025",  "Début du 1er trimestre",          "CALENDRIER",  etabId);
            saveParam("T1_FIN",           "31/12/2025",  "Fin du 1er trimestre",            "CALENDRIER",  etabId);
            saveParam("T2_DEBUT",         "06/01/2026",  "Début du 2e trimestre",           "CALENDRIER",  etabId);
            saveParam("T2_FIN",           "31/03/2026",  "Fin du 2e trimestre",             "CALENDRIER",  etabId);
            saveParam("T3_DEBUT",         "14/04/2026",  "Début du 3e trimestre",           "CALENDRIER",  etabId);
            saveParam("T3_FIN",           "30/06/2026",  "Fin du 3e trimestre",             "CALENDRIER",  etabId);
            saveParam("EXAMEN_T1",        "15/12/2025",  "Date des examens T1",             "CALENDRIER",  etabId);
            saveParam("EXAMEN_T2",        "20/03/2026",  "Date des examens T2",             "CALENDRIER",  etabId);
            saveParam("EXAMEN_FINAL",     "15/06/2026",  "Date des examens de fin d'année", "CALENDRIER",  etabId);
        }

        // 8. Paramètres identité école (idempotents)
        upsertParam("ADRESSE",            "",           "Adresse de l'établissement",    "ETABLISSEMENT", etabId);
        upsertParam("TELEPHONE_ECOLE",    "",           "Téléphone",                     "ETABLISSEMENT", etabId);
        upsertParam("EMAIL_ECOLE",        "",           "Email institutionnel",          "ETABLISSEMENT", etabId);
        upsertParam("CONTACT_PRINCIPAL",  "",           "Directeur / Contact principal", "ETABLISSEMENT", etabId);
        upsertParam("DEVISE",             "",           "Devise / Credo de l'école",     "ETABLISSEMENT", etabId);
        upsertParam("MONNAIE",            "FCFA",       "Monnaie utilisée",              "ETABLISSEMENT", etabId);
        upsertParam("TYPE_MATERNELLE",    "false",      "Type Maternelle",               "ETABLISSEMENT", etabId);
        upsertParam("TYPE_PRIMAIRE",      "false",      "Type Primaire",                 "ETABLISSEMENT", etabId);
        upsertParam("TYPE_COLLEGE",       "true",       "Type Collège",                  "ETABLISSEMENT", etabId);
        upsertParam("TYPE_LYCEE",         "false",      "Type Lycée",                    "ETABLISSEMENT", etabId);
        upsertParam("TYPE_LYCEE_GENERAL", "false",      "Type Lycée Général",            "ETABLISSEMENT", etabId);
        upsertParam("DECOUPAGE",          "TRIMESTRES", "Découpage calendaire",          "CALENDRIER",    etabId);

        // 9. Frais scolaires
        if (fraisRepository.count() == 0) {
            saveFrais("Frais d'inscription",       "INSCRIPTION", 50000.0, "Septembre",    null, true,  etabId);
            saveFrais("Scolarité – 1er trimestre", "SCOLARITE",   75000.0, "Octobre",      null, true,  etabId);
            saveFrais("Scolarité – 2e trimestre",  "SCOLARITE",   75000.0, "Janvier",      null, true,  etabId);
            saveFrais("Scolarité – 3e trimestre",  "SCOLARITE",   75000.0, "Avril",        null, true,  etabId);
            saveFrais("Transport scolaire",        "TRANSPORT",   30000.0, "Par trimestre",null, false, etabId);
        }

        // 10. Lier compte élève KONAN Amara
        if (eleveRepository.findByCompteEmail("eleve@holyflame.com").isEmpty()) {
            eleveRepository.findAll().stream()
                .filter(e -> "KONAN".equals(e.getNom()) && "Amara".equals(e.getPrenom()))
                .findFirst()
                .ifPresent(e -> { e.setCompteEmail("eleve@holyflame.com"); eleveRepository.save(e); });
        }
    }

    private void saveUser(String nom, String prenom, String email, String mdp, String role, Etablissement etab) {
        Utilisateur u = new Utilisateur();
        u.setNom(nom); u.setPrenom(prenom); u.setEmail(email);
        u.setMotDePasse(passwordEncoder.encode(mdp)); u.setRole(role);
        u.setEtablissement(etab);
        utilisateurRepository.save(u);
    }

    private void saveClasse(String nom, String niveau, String annee, Long etabId) {
        Classe c = new Classe();
        c.setNom(nom); c.setNiveau(niveau); c.setAnneeScolaire(annee);
        c.setEtablissementId(etabId);
        classeRepository.save(c);
    }

    private void saveMatiere(String nom, Double coef, String desc, Long etabId) {
        Matiere m = new Matiere();
        m.setNom(nom); m.setCoefficient(coef); m.setDescription(desc);
        m.setEtablissementId(etabId);
        matiereRepository.save(m);
    }

    private void saveEleve(String mat, String nom, String prenom, LocalDate ddn,
                           String tel, String email, Classe classe, Long etabId) {
        Eleve e = new Eleve();
        e.setMatricule(mat); e.setNom(nom); e.setPrenom(prenom);
        e.setDateNaissance(ddn); e.setTelephoneParent(tel);
        e.setEmailParent(email); e.setStatutInscription("INSCRIT");
        e.setClasse(classe); e.setEtablissementId(etabId);
        eleveRepository.save(e);
    }

    private void saveParam(String cle, String valeur, String description, String categorie, Long etabId) {
        Parametre p = new Parametre();
        p.setCle(cle); p.setValeur(valeur);
        p.setDescription(description); p.setCategorie(categorie);
        p.setEtablissementId(etabId);
        parametreRepository.save(p);
    }

    private void upsertParam(String cle, String defVal, String desc, String cat, Long etabId) {
        if (!parametreRepository.existsByCleAndEtablissementId(cle, etabId)) {
            Parametre p = new Parametre();
            p.setCle(cle); p.setValeur(defVal);
            p.setDescription(desc); p.setCategorie(cat);
            p.setEtablissementId(etabId);
            parametreRepository.save(p);
        }
    }

    private void saveFrais(String designation, String type, Double montant,
                           String echeance, String niveau, boolean obligatoire, Long etabId) {
        FraisScolarite f = new FraisScolarite();
        f.setDesignation(designation); f.setTypeFrais(type);
        f.setMontant(montant); f.setEcheance(echeance);
        f.setNiveauCible(niveau); f.setObligatoire(obligatoire);
        f.setEtablissementId(etabId);
        fraisRepository.save(f);
    }
}
