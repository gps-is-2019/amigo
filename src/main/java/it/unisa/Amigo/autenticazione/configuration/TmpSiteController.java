package it.unisa.Amigo.autenticazione.configuration;


import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.transaction.Transactional;


@Controller
public class TmpSiteController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SupergruppoDAO supergruppoDAO;

    @Autowired
    private PersonaDAO personaDAO;

    //@Autowired
    private User user;

    //@Autowired
    private Persona persona;


    /**
     *
     * @param model
     * @return
     */
    @GetMapping("/dashboard")
    @Transactional
    public String getDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        persona = personaDAO.findByUser_email(auth.getName());
        model.addAttribute("idPersona", persona.getId());
        return "/dashboard";
    }
}
