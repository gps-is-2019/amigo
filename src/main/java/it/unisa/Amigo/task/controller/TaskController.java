package it.unisa.Amigo.task.controller;


import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.TaskForm;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    //TODO da vedere come prendere l'utente corrente
    @Autowired
    private PersonaDAO personaDAO;


    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}")
    public String visualizzaListaTaskSupergruppo(Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) {
        model.addAttribute("idSupergruppo" , Integer.toString(idSupergruppo) );
        model.addAttribute("listaTask",taskService.visualizzaTaskSuperGruppo(idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        System.out.println(personaLoggata.getNome() + "is responsabile = " + gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

        return "task/paginaVisualizzaListaTaskSupergruppo";
    }

    //action form
    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/definizioneTaskSupergruppo")
    public String definizioneTaskSupergruppo( @ModelAttribute Task taskForm, Model model ,  @PathVariable(name = "idSupergruppo") int idSupergruppo){

        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("taskForm", taskForm);

        List<Persona> persone = gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("persone" , persone);
        System.out.println(persone.toString());


        return "task/paginaDefinizioneTaskSupergruppo";//pagina che usa il form (pagina corrente)

    }

  //  @RequestMapping(value="/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/creazioneTaskSupergruppo", method=RequestMethod.GET)
  //  public String savePerson(@ModelAttribute TaskForm taskForm, Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) {
  //      model.addAttribute("taskForm", taskForm);
  //      System.out.println(" sono dentro 1");
  //      return "task/paginaDefinizioneTaskSupergruppo";
  //  }

    @RequestMapping(value="/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/creazioneTaskSupergruppo", method=RequestMethod.POST)
    public String saveTaskPost(@ModelAttribute TaskForm taskForm , Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) throws ParseException {

        System.out.println(taskForm.toString());
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        Date tmpData = formatter.parse(taskForm.getDataScadenza());
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        Persona persona = gruppoService.findPersona( taskForm.getIdPersona() );
        Boolean flagCreazione = taskService.definizioneTaskSupergruppo(taskForm.getDescrizione(), tmpData, taskForm.getNome(), "incompleto", supergruppo, persona);
        List<Persona> persone = gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("persone" , persone);
        model.addAttribute("flagCreazione",flagCreazione);
        //return "redirect:task/paginaVisualizzaListaTaskSupergruppo";
        return "task/paginaDefinizioneTaskSupergruppo";
    }

    //TODO  da vedere come prendere correttamente l'utente corrente lo sto prendendo tramite grupposervice

    @Autowired
    private GruppoService gruppoService;

    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}")
    public String visualizzaDettagliTaskSupergruppo(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask
    ) {
        //TODO
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        System.out.println(personaLoggata.getNome() + "is responsabile = " + gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("task" , taskService.getTaskById(idTask));
        System.out.println(taskService.getTaskById(idTask));

        return "task/paginaDettagliTaskSupergruppo";
    }


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



    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}/approvazione")
    String approvazioneTask(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        System.out.println(personaLoggata.getNome() + "is responsabile = " + gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("task" , taskService.getTaskById(idTask));

        taskService.accettazioneTask(idTask);
        model.addAttribute("flagAzione",1);
        return "task/paginaDettagliTaskSupergruppo";
    }


    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}/rifiuta")
    String rifiutoTask(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        System.out.println(personaLoggata.getNome() + "is responsabile = " + gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("task" , taskService.getTaskById(idTask));

        taskService.rifiutoTask(idTask);
        model.addAttribute("flagAzione",2);
        return "task/paginaDettagliTaskSupergruppo";
    }

    //TODO
    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}/modifica")
    String modificaTask(@ModelAttribute TaskForm taskForm, Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Task task = taskService.getTaskById(idTask);
        taskForm.setId(task.getId());
        taskForm.setNome(task.getNome());
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        String strDate = dateFormat.format(task.getDataScadenza());
        taskForm.setDataScadenza(strDate);
        taskForm.setDescrizione(task.getDescrizione());
        taskForm.setStato(task.getStato());
        Persona persona = task.getPersona();
        taskForm.setIdPersona(persona.getId());
        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("taskForm", taskForm);

        List<Persona> persone = gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("persone" , persone);
        System.out.println(persone.toString());


        return "task/paginaModificaTask";
    }

}