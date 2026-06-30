package holyflame.administration.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleFkConstraint(HttpServletRequest req, DataIntegrityViolationException ex) {
        ModelAndView mav = new ModelAndView("erreur-suppression");
        String referer = req.getHeader("Referer");
        mav.addObject("retourUrl", referer != null ? referer : "/dashboard");
        String msg = ex.getMessage();
        if (msg != null && msg.contains("FOREIGN KEY")) {
            mav.addObject("detail", "Cet élément est utilisé par d'autres données (notes, absences, paiements…). Supprimez les données liées en premier.");
        } else {
            mav.addObject("detail", "Une contrainte de base de données empêche cette suppression.");
        }
        return mav;
    }
}
