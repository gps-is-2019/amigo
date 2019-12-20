package it.unisa.Amigo.autenticazione.configuration;


import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

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


    @GetMapping("/dashboard")
    @Transactional
    public String getAllSites(Model model) {
        return "/dashboard";
    }

}
