package it.unisa.Amigo.repository.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public class RepositoryController {
    //submit form
    @PostMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/definizioneTaskSupergruppo")
    public String submitDefinizioneTaskSupergruppo(Model model , @PathVariable(name = "idSupergruppo") int idSupergruppo){
        //model.addAttribute("aggiunto" , "messaggio: aggiunta task e riassunto aggiunta");
        System.out.println(model.getAttribute("testo")) ;


        return "task/paginaDefinizioneTaskSupergruppo"; // pagina che viene visuliazzata dopo il submit
    }
}
