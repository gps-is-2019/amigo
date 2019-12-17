package it.unisa.Amigo.autenticazione.configuration;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TmpSiteController {

    @GetMapping("/dashboard")
    public String getAllSites(Model model) {
        //model.addAttribute("sites",allSites);
        return "/dashboard";
    }

}
