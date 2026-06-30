package holyflame.administration.service;

import holyflame.administration.model.Etablissement;
import holyflame.administration.model.Utilisateur;
import holyflame.administration.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class EtablissementService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur getCurrentUtilisateur() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return utilisateurRepository.findByEmail(auth.getName()).orElse(null);
    }

    public Etablissement getCurrentEtablissement() {
        Utilisateur u = getCurrentUtilisateur();
        return u != null ? u.getEtablissement() : null;
    }

    public Long getCurrentEtablissementId() {
        Etablissement e = getCurrentEtablissement();
        return e != null ? e.getId() : null;
    }

    public boolean isSuperAdmin() {
        Utilisateur u = getCurrentUtilisateur();
        return u != null && "SUPER_ADMIN".equals(u.getRole());
    }
}
