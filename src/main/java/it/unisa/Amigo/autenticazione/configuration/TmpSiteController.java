package it.unisa.Amigo.autenticazione.configuration;


import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.transaction.Transactional;


@Controller
public class TmpSiteController {

    @Autowired
    private PersonaDAO personaDAO;

    private Persona persona;

    /**
     *
     * @param model
     * @return
     */
    @GetMapping("/dashboard")
    @Transactional
    public String getDashboard(final Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        persona = personaDAO.findByUser_email(auth.getName());
        model.addAttribute("idPersona", persona.getId());
        return "dashboard";
    }

    @GetMapping("/login")
    public String getLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        persona = personaDAO.findByUser_email(auth.getName());
        return "login_page";
    }

    @GetMapping("/logout")
    public String getLogout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        persona = personaDAO.findByUser_email(auth.getName());
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/";
    }

}
