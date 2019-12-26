package it.unisa.Amigo.task.controller;


import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;


    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}")
    public String visualizzaListaTaskSupergruppo(Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) {
        model.addAttribute("idSupergruppo" , Integer.toString(idSupergruppo) );
        model.addAttribute("listaTask",taskService.visualizzaTaskSuperGruppo(idSupergruppo));
        return "task/paginaVisualizzaListaTaskSupergruppo";
    }

    //action form
    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/definizioneTaskSupergruppo")
    public String definizioneTaskSupergruppo( Model model ,  @PathVariable(name = "idSupergruppo") int idSupergruppo){

//        model.addAttribute("idSupergruppo" , Integer.toString(idSupergruppo) );
        model.addAttribute("idSupergruppo" , idSupergruppo );

        return "task/paginaDefinizioneTaskSupergruppo";//pagina che usa il form (pagina corrente)

    }

    //submit form
    @PostMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/definizioneTaskSupergruppo")
    public String submitDefinizioneTaskSupergruppo( Model model ,  @PathVariable(name = "idSupergruppo") int idSupergruppo){
        //model.addAttribute("aggiunto" , "messaggio: aggiunta task e riassunto aggiunta");
        System.out.println(model.getAttribute("testo")) ;


        return "task/paginaDefinizioneTaskSupergruppo"; // pagina che viene visuliazzata dopo il submit
    }

    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}")
    public String visualizzaDettagliTaskSupergruppo(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask

    ) {

        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("task" , taskService.getTaskById(idTask));
        System.out.println(taskService.getTaskById(idTask));


        return "task/paginaDettagliTaskSupergruppo";

    }


    //TODO da vedere come prendere l'utente corrente
    @Autowired
    private PersonaDAO personaDAO;



    @GetMapping("/taskPersonali")
    public String visualizzaListaTaskPersonali(Model model) {

        //Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        //model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        //TODO da vedere come prendere l'utente corrente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Persona personaLoggata = personaDAO.findByUser_email(auth.getName());
        List<Task> ris = taskService.visualizzaTaskUser(personaLoggata.getId());
        System.out.println(ris);
        model.addAttribute("listaTask" , ris);


        return "task/paginaVisualizzaListaTaskPersonali";
    }



}