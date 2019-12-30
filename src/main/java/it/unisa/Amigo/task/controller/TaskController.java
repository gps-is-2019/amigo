package it.unisa.Amigo.task.controller;


import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private GruppoService gruppoService;

    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}")
    public String visualizzaListaTaskSupergruppo(Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) {
        //TODO da vedere come prendere l'utente corrente
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

        model.addAttribute("idSupergruppo" , Integer.toString(idSupergruppo) );
        model.addAttribute("listaTask",taskService.visualizzaTaskSuperGruppo(idSupergruppo));

        return "task/paginaVisualizzaListaTaskSupergruppo";
    }

    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/definizioneTaskSupergruppo")
    public String definizioneTaskSupergruppo( @ModelAttribute Task taskForm, Model model ,  @PathVariable(name = "idSupergruppo") int idSupergruppo){

        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("taskForm", taskForm);

        List<Persona> persone = gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("persone" , persone);

        return "task/paginaDefinizioneTaskSupergruppo";//pagina che usa il form (pagina corrente)
    }

    @RequestMapping(value="/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/creazioneTaskSupergruppo", method=RequestMethod.POST)
    public String saveTaskPost(@ModelAttribute TaskForm taskForm , Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) throws ParseException {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        Date tmpData;
        System.out.println("-------------"+ taskForm.getDataScadenza());
        if(taskForm.getDataScadenza().equals("")) {
            tmpData = formatter.parse("1-1-2021");
        }
        else {
            tmpData= formatter.parse(taskForm.getDataScadenza());
        }
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        Persona persona = gruppoService.findPersona( taskForm.getIdPersona() );
        if (       ( (taskForm.getNome()==null) || (taskForm.getNome().equals("")) )
                || ( (taskForm.getDataScadenza()==null) || (taskForm.getDataScadenza().equals("")) )
                || ( (taskForm.getDescrizione()==null) || (taskForm.getDescrizione().equals("")) )
                || ( (taskForm.getIdPersona() < 0 ) )){
            List<Persona> persone = gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
            model.addAttribute("persone", persone);
            model.addAttribute("flagCreazione" , false);
            return "task/paginaDefinizioneTaskSupergruppo";
        }
        Boolean flagCreazione = taskService.definizioneTaskSupergruppo(taskForm.getDescrizione(), tmpData, taskForm.getNome(), "incompleto", supergruppo, persona);
        List<Persona> persone = gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("persone", persone);
        model.addAttribute("flagCreazione", flagCreazione);
        return "task/paginaDefinizioneTaskSupergruppo";
    }

    //TODO  da vedere come prendere correttamente l'utente corrente lo sto prendendo tramite grupposervice
    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}")
    public String visualizzaDettagliTaskSupergruppo(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask ) {
        //TODO da vedere come prendere l'utente corrente
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("task" , taskService.getTaskById(idTask));

        return "task/paginaDettagliTaskSupergruppo";
    }


    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}/approvazione")
    String approvazioneTask(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

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

    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}/completa")
    String completaTask(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));
        System.out.println(personaLoggata.getNome() + "is responsabile = " + gruppoService.isResponsabile(personaLoggata.getId(),idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("task" , taskService.getTaskById(idTask));

        taskService.completaTask(idTask);
        return "task/paginaDettagliTaskSupergruppo";
    }


    @GetMapping("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}/modifica")
    String modificaTask(@ModelAttribute TaskForm taskForm, Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {

        Task task = taskService.getTaskById(idTask);
        taskForm.setId(task.getId());
        taskForm.setNome(task.getNome());
        taskForm.setDataScadenza(task.getDataScadenza().toString().substring(0, 10));
        taskForm.setDescrizione(task.getDescrizione());
        taskForm.setStato(task.getStato());
        Persona persona = task.getPersona();
        taskForm.setIdPersona(persona.getId());
        model.addAttribute("idTask" , idTask);
        model.addAttribute("idSupergruppo" , idSupergruppo );
        model.addAttribute("taskForm", taskForm);

        List<Persona> persone = gruppoService.visualizzaListaMembriSupergruppo(idSupergruppo);
        model.addAttribute("persone" , persone);
        return "task/paginaModificaTask";
    }


    @RequestMapping(value = "/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/dettagliTaskSupergruppo{idTask}/modificaTaskSupergruppo")
    public String saveModifyTask(@ModelAttribute TaskForm taskForm , Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) throws ParseException {
        Task taskToUpdate = taskService.getTaskById(taskForm.getId());
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        Date tmpData = null;
        if(taskForm.getDataScadenza().equals("")) {
            tmpData = formatter.parse(taskToUpdate.getDataScadenza().toString());
        }
        else{
            tmpData= formatter.parse(taskForm.getDataScadenza());
        }
        taskToUpdate.setDataScadenza(tmpData);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        taskToUpdate.setSupergruppo(supergruppo);
        Persona persona = gruppoService.findPersona( taskForm.getIdPersona() );
        taskToUpdate.setPersona(persona);
        taskToUpdate.setNome(taskForm.getNome());
        taskToUpdate.setDescrizione(taskForm.getDescrizione());
        model.addAttribute("flagAzione",3);
        taskService.updateTask(taskToUpdate);
        model.addAttribute("task" , taskToUpdate);
        return "task/paginaDettagliTaskSupergruppo";
    }

    @GetMapping("/taskPersonali")
    public String visualizzaListaTaskPersonali(Model model) {
        //TODO da vedere come prendere l'utente corrente
        Persona personaLoggata = gruppoService.visualizzaPersonaLoggata();
        List<Task> ris = taskService.visualizzaTaskUser(personaLoggata.getId());
        model.addAttribute("listaTask" , ris);

        return "task/paginaVisualizzaListaTaskPersonali";
    }

    @GetMapping("/taskPersonali/dettagliTask{idTask}")
    public String visualizzaDettagliTaskPersonali(Model model , @PathVariable(name = "idTask") int idTask) {
        Task task = taskService.getTaskById(idTask);
        model.addAttribute("task" , task);
        return "task/paginaDettagliTaskPersonali";
    }

}