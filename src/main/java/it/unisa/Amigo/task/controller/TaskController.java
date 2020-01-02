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
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Questa classe si occupa della logica di controllo del sottosistema Task
 */
@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private GruppoService gruppoService;

    /**
     * Ritorna ad una pagina i task @{@link Task} di un supergruppo @{@link Supergruppo}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui i task da visualizzare appartengono
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks")
    public String visualizzaListaTaskSupergruppo(Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) {
        //TODO da vedere come prendere l'utente corrente
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));

        model.addAttribute("idSupergruppo", Integer.toString(idSupergruppo));
        model.addAttribute("listaTask", taskService.visualizzaTaskSuperGruppo(idSupergruppo));

        return "task/paginaVisualizzaListaTaskSupergruppo";
    }

    /**
     * Permette di definire un task @{@link Task}
     *
     * @param taskForm      contiene le informazioni da inserire nel task
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da definire appartine
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/creaTask")
    public String definizioneTaskSupergruppo(@ModelAttribute Task taskForm, Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) {

        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("taskForm", taskForm);

        List<Persona> persone = gruppoService.findAllMembriInSupergruppo(idSupergruppo);
        model.addAttribute("persone", persone);

        return "task/paginaDefinizioneTaskSupergruppo";//pagina che usa il form (pagina corrente)
    }

    /**
     * Permette di salvare il task @{@link Task}
     *
     * @param taskForm      contiene le informazioni da inserire nel task
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da salvare appartine
     * @return il path della pagina su cui eseguire il redirect
     * @throws ParseException
     */
    @RequestMapping(value = "/gruppi/{idSupergruppo}/tasks/creazioneTask", method = RequestMethod.POST)
    public String saveTaskPost(@ModelAttribute TaskForm taskForm, Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) throws ParseException {
        LocalDate tmpData;

        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        Persona persona = gruppoService.findPersona(taskForm.getIdPersona());
        if (((taskForm.getNome() == null) || (taskForm.getNome().equals("")))
                || ((taskForm.getDataScadenza() == null) || (taskForm.getDataScadenza().equals("")))
                || ((taskForm.getDescrizione() == null) || (taskForm.getDescrizione().equals("")))
                || ((taskForm.getIdPersona() < 0))) {
            List<Persona> persone = gruppoService.findAllMembriInSupergruppo(idSupergruppo);
            model.addAttribute("persone", persone);
            model.addAttribute("flagCreazione", false);
            return "task/paginaDefinizioneTaskSupergruppo";
        }
        tmpData = LocalDate.of(Integer.parseInt(taskForm.getDataScadenza().substring(0,4)), Integer.parseInt(taskForm.getDataScadenza().substring(5,7)) , Integer.parseInt(taskForm.getDataScadenza().substring(8,10)));
        Boolean flagCreazione = taskService.definizioneTaskSupergruppo(taskForm.getDescrizione(), tmpData, taskForm.getNome(), "incompleto", supergruppo, persona);
        List<Persona> persone = gruppoService.findAllMembriInSupergruppo(idSupergruppo);
        model.addAttribute("persone", persone);
        model.addAttribute("flagCreazione", flagCreazione);
        return "task/paginaDefinizioneTaskSupergruppo";
    }

    //TODO  da vedere come prendere correttamente l'utente corrente lo sto prendendo tramite grupposervice

    /**
     * Ritorna ad una pagina i dettagli di uno specifico  task @{@link Task} di un supergruppo @{@link Supergruppo}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task di cui si vogliono visualizzare i dettagli appartine
     * @param idTask        identifica univocamente un task di cui si vogliono visualizzare i dettagli
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}")
    public String visualizzaDettagliTaskSupergruppo(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        //TODO da vedere come prendere l'utente corrente
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));

        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));

        return "task/paginaDettagliTaskSupergruppo";
    }


    /**
     * Permette di approvare un task @{@link Task}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da approvare appartine
     * @param idTask        identifica univocamente un task che si vuole approvare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva")
    String approvazioneTask(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));

        taskService.accettazioneTask(idTask);
        model.addAttribute("flagAzione", 1);
        return "task/paginaDettagliTaskSupergruppo";
    }


    /**
     * Permette di rifiutare un task @{@link Task}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da rifiutare appartine
     * @param idTask        identifica univocamente un task che si vuole rifiutare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/rifiuta")
    String rifiutoTask(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));

        taskService.rifiutoTask(idTask);
        model.addAttribute("flagAzione", 2);
        return "task/paginaDettagliTaskSupergruppo";
    }

    /**
     * Permette di completare un task @{@link Task}
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da completare  appartine
     * @param idTask        identifica univocamente un task che si vuole completare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/completa")
    String completaTask(Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));

        //TODO da vedere come prendere l'utente corrente
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));

        taskService.completaTask(idTask);
        return "task/paginaDettagliTaskSupergruppo";
    }


    /**
     * Permette di modificare un task @{@link Task}
     *
     * @param taskForm      contiene le informazioni per modificare il task
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da modificare  appartine
     * @param idTask        identifica univocamente un task che si vuole modificare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modifica")
    String modificaTask(@ModelAttribute TaskForm taskForm, Model model
            , @PathVariable(name = "idSupergruppo") int idSupergruppo
            , @PathVariable(name = "idTask") int idTask) {

        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        Task task = taskService.getTaskById(idTask);
        taskForm.setId(task.getId());
        taskForm.setNome(task.getNome());
        taskForm.setDataScadenza(task.getDataScadenza().toString().substring(0, 10));
        taskForm.setDescrizione(task.getDescrizione());
        taskForm.setStato(task.getStato());
        Persona persona = task.getPersona();
        taskForm.setIdPersona(persona.getId());
        model.addAttribute("idTask", idTask);
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));
        model.addAttribute("taskForm", taskForm);

        List<Persona> persone = gruppoService.findAllMembriInSupergruppo(idSupergruppo);
        model.addAttribute("persone", persone);
        return "task/paginaModificaTask";
    }

    /**
     * Permette di salvare le modifiche apportante ad  un task @{@link Task}
     *
     * @param taskForm      contiene le informazioni da salvare
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task modificato appartine  appartine
     * @return il path della pagina su cui eseguire il redirect
     * @throws ParseException
     */
    @RequestMapping(value = "/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modificaTask")
    public String saveModifyTask(@ModelAttribute TaskForm taskForm, Model model, @PathVariable(name = "idSupergruppo") int idSupergruppo) throws ParseException {
        Task taskToUpdate = taskService.getTaskById(taskForm.getId());
        LocalDate tmpData;
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        tmpData = LocalDate.of(Integer.parseInt(taskForm.getDataScadenza().substring(0, 4)), Integer.parseInt(taskForm.getDataScadenza().substring(5, 7)), Integer.parseInt(taskForm.getDataScadenza().substring(8, 10)));

        taskToUpdate.setDataScadenza(tmpData);
        Supergruppo supergruppo = gruppoService.findSupergruppo(idSupergruppo);
        taskToUpdate.setSupergruppo(supergruppo);
        Persona persona = gruppoService.findPersona(taskForm.getIdPersona());
        taskToUpdate.setPersona(persona);
        taskToUpdate.setNome(taskForm.getNome());
        taskToUpdate.setDescrizione(taskForm.getDescrizione());
        model.addAttribute("flagAzione", 3);
        taskService.updateTask(taskToUpdate);
        model.addAttribute("isResponsabile", gruppoService.isResponsabile(personaLoggata.getId(), idSupergruppo));
        model.addAttribute("task", taskToUpdate);
        return "task/paginaDettagliTaskSupergruppo";
    }

    /**
     * Ritorna ad una pagina i task @{@link Task} di una persona @{@link Persona}
     *
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/taskPersonali")
    public String visualizzaListaTaskPersonali(Model model) {
        //TODO da vedere come prendere l'utente corrente
        Persona personaLoggata = gruppoService.getAuthenticatedUser();
        List<Task> ris = taskService.visualizzaTaskUser(personaLoggata.getId());
        model.addAttribute("listaTask", ris);

        return "task/paginaVisualizzaListaTaskPersonali";
    }

    /**
     * Ritorna ad una pagina i dettagli di un  task @{@link Task} di una persona @{@link Persona}
     *
     * @param model  per salvare informazioni da recuperare nell'html
     * @param idTask identifica univocamente un task di cui si vogliono visualizzare i dettagli
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/taskPersonali/task_detail/{idTask}")
    public String visualizzaDettagliTaskPersonali(Model model, @PathVariable(name = "idTask") int idTask) {
        Task task = taskService.getTaskById(idTask);
        model.addAttribute("task", task);
        return "task/paginaDettagliTaskPersonali";
    }

    /**
     * Permette di completare un task @{@link Task} personale
     *
     * @param model  per salvare informazioni da recuperare nell'html
     * @param idTask identifica univocamente un task che si vuole completare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/taskPersonali/task_detail/{idTask}/completa")
    String completaTaskPersonale(Model model
            , @PathVariable(name = "idTask") int idTask) {
        model.addAttribute("task", taskService.getTaskById(idTask));
        taskService.completaTask(idTask);
        model.addAttribute("flagAzione", 1);

        return "task/paginaDettagliTaskPersonali";
    }

}