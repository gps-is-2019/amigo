package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Questa classe si occupa della logica di controllo del sottosistema Task.
 */
@Controller
@RequiredArgsConstructor
public class TaskController {

    /**
     * Gestisce la logica del sottosistema Task.
     */
    private final TaskService taskService;

    private static final int FLAG_APPROVA = 1;
    private static final int FLAG_RIFIUTA = 2;
    private static final int FLAG_MODIFICA = 3;
    private static final int FLAG_COMPLETA = 4;

    /**
     * Ritorna ad una pagina i task @{@link Task} di un supergruppo.
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui i task da visualizzare appartengono
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks")
    public String visualizzaListaTaskSupergruppo(final Model model, @PathVariable(name = "idSupergruppo") final int idSupergruppo) {
        model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
        model.addAttribute("idSupergruppo", Integer.toString(idSupergruppo));
        model.addAttribute("listaTask", taskService.visualizzaTaskSuperGruppo(idSupergruppo));
        List<Documento> listaDocumenti = taskService.getApprovedDocumentiOfSupergruppo(idSupergruppo);
        model.addAttribute("documenti", listaDocumenti);

        return "task/tasks_supergruppo";
    }

    /**
     * Permette di definire un task @{@link Task}.
     *
     * @param taskForm      contiene le informazioni da inserire nel task
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da definire appartine
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/create")
    public String definizioneTaskSupergruppo(@ModelAttribute final Task taskForm, final Model model,
                                             @PathVariable(name = "idSupergruppo") final int idSupergruppo) {

        if (!taskService.currentPersonaCanCreateTask(idSupergruppo)) {
            return "redirect:/403";
        }
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("taskForm", taskForm);
        List<Persona> persone = taskService.getPossibleTaskAssegnees(idSupergruppo);
        model.addAttribute("persone", persone);

        return "task/crea_task";
    }

    /**
     * Permette di salvare il task @{@link Task}.
     *
     * @param taskForm      contiene le informazioni da inserire nel task
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da salvare appartine
     * @return il path della pagina su cui eseguire il redirect
     */
    @PostMapping(value = "/gruppi/{idSupergruppo}/tasks/create")
    public String saveTaskPost(@ModelAttribute final TaskForm taskForm, final Model model,
                               @PathVariable(name = "idSupergruppo") final int idSupergruppo) {
        if (!taskVerify(taskForm)) {
            List<Persona> persone = taskService.getPossibleTaskAssegnees(idSupergruppo);
            model.addAttribute("persone", persone);
            model.addAttribute("flagCreazione", false);
            return "task/crea_task";
        } else {
            LocalDate date = LocalDate.parse(taskForm.getDataScadenza());
            Task task = taskService.definizioneTaskSupergruppo(taskForm.getDescrizione(),
                    date, taskForm.getNome(), "incompleto", idSupergruppo, taskForm.getIdPersona());
            model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
            model.addAttribute("idSupergruppo", idSupergruppo);
            model.addAttribute("task", task);
            model.addAttribute("flagCreazione", true);
            return "task/dettagli_task_supergruppo";
        }
    }

    /**
     * Controlla che i parametri immessi nel form siano corretti.
     *
     * @param taskForm valori immessi nel form
     * @return true se i parametri sono corretti, false altrimenti
     */
    private boolean taskVerify(final TaskForm taskForm) {
        boolean dateIsAfter = false;

        if (!taskForm.getDataScadenza().equals("")) {
            LocalDate date = LocalDate.parse(taskForm.getDataScadenza());
            dateIsAfter = date.plusDays(1).isAfter(LocalDate.now());
        }
        boolean descrizioneMatch = taskForm.getDescrizione().matches("^(?!^.{255})^(.|\\s)*[a-zA-Z]+(.|\\s)*$");
        boolean nomeMatch = taskForm.getNome().matches("^(?!^.{100})^(.|\\s)*[a-zA-Z]+(.|\\s)*$");

        return nomeMatch && descrizioneMatch && dateIsAfter && (taskForm.getIdPersona() != null);
    }

    /**
     * Ritorna ad una pagina i dettagli di uno specifico  task @{@link Task} di un supergruppo.
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task di cui si vogliono visualizzare i dettagli appartine
     * @param idTask        identifica univocamente un task di cui si vogliono visualizzare i dettagli
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}")
    public String visualizzaDettagliTaskSupergruppo(final Model model,
                                                    @PathVariable(name = "idSupergruppo") final int idSupergruppo,
                                                    @PathVariable(name = "idTask") final int idTask) {
        model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));

        return "task/dettagli_task_supergruppo";
    }

    /**
     * Permette di approvare un task @{@link Task}.
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da approvare appartine
     * @param idTask        identifica univocamente un task che si vuole approvare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva")
    public String approvazioneTask(final Model model,
                                   @PathVariable(name = "idSupergruppo") final int idSupergruppo,
                                   @PathVariable(name = "idTask") final int idTask) {
        model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));
        taskService.accettazioneTask(idTask);
        model.addAttribute("flagAzione", FLAG_APPROVA);
        return "task/dettagli_task_supergruppo";
    }


    /**
     * Permette di rifiutare un task @{@link Task}.
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da rifiutare appartine
     * @param idTask        identifica univocamente un task che si vuole rifiutare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/rifiuta")
    public String rifiutoTask(final Model model,
                              @PathVariable(name = "idSupergruppo") final int idSupergruppo,
                              @PathVariable(name = "idTask") final int idTask) {
        model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));
        taskService.rifiutoTask(idTask);
        model.addAttribute("flagAzione", FLAG_RIFIUTA);

        return "task/dettagli_task_supergruppo";
    }

    /**
     * Permette di completare un task @{@link Task}.
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da completare  appartine
     * @param idTask        identifica univocamente un task che si vuole completare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/completa")
    public String completaTask(final Model model,
                               @PathVariable(name = "idSupergruppo") final int idSupergruppo,
                               @PathVariable(name = "idTask") final int idTask) {
        model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("task", taskService.getTaskById(idTask));
        model.addAttribute("flagAzione", FLAG_COMPLETA);

        taskService.completaTask(idTask);
        return "task/dettagli_task_supergruppo";
    }


    /**
     * Permette di modificare un task @{@link Task}.
     *
     * @param taskForm      contiene le informazioni per modificare il task
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task da modificare  appartine
     * @param idTask        identifica univocamente un task che si vuole modificare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modifica")
    public String modificaTask(@ModelAttribute final TaskForm taskForm, final Model model,
                               @PathVariable(name = "idSupergruppo") final int idSupergruppo,
                               @PathVariable(name = "idTask") final int idTask) {

        Task task = taskService.getTaskById(idTask);
        taskForm.setId(task.getId());
        taskForm.setNome(task.getNome());
        taskForm.setDataScadenza(task.getDataScadenza().toString());
        taskForm.setDescrizione(task.getDescrizione());
        taskForm.setStato(task.getStato());
        Persona persona = task.getPersona();
        taskForm.setIdPersona(persona.getId());
        model.addAttribute("idTask", idTask);
        model.addAttribute("idSupergruppo", idSupergruppo);
        model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
        model.addAttribute("taskForm", taskForm);

        List<Persona> persone = taskService.getPossibleTaskAssegnees(idSupergruppo);
        model.addAttribute("persone", persone);
        return "task/modifica_task";
    }

    /**
     * Permette di salvare le modifiche apportante ad  un task @{@link Task}.
     *
     * @param taskForm      contiene le informazioni da salvare
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo id del supergruppo a cui il task modificato appartine  appartine
     * @return il path della pagina su cui eseguire il redirect
     */
    @RequestMapping(value = "/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modificaTask")
    public String saveModifyTask(@ModelAttribute final TaskForm taskForm, final Model model,
                                 @PathVariable(name = "idSupergruppo") final int idSupergruppo,
                                 @PathVariable(name = "idTask") final int idTask) {
        if (taskVerify(taskForm)) {

            Task taskToUpdate = taskService.getTaskById(taskForm.getId());
            LocalDate date = LocalDate.parse(taskForm.getDataScadenza());
            taskToUpdate.setDataScadenza(date);

            taskToUpdate.setNome(taskForm.getNome());

            taskToUpdate.setDescrizione(taskForm.getDescrizione());

            taskService.updateTask(taskToUpdate, taskForm.getIdPersona());
            model.addAttribute("flagAzione", FLAG_MODIFICA);
            model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
            model.addAttribute("task", taskToUpdate);
            return "task/dettagli_task_supergruppo";

        } else {

            model.addAttribute("flagCreazione", false);
            Task task = taskService.getTaskById(idTask);
            taskForm.setId(task.getId());
            taskForm.setNome(task.getNome());
            taskForm.setDataScadenza(task.getDataScadenza().toString());
            taskForm.setDescrizione(task.getDescrizione());
            taskForm.setStato(task.getStato());
            Persona persona = task.getPersona();
            taskForm.setIdPersona(persona.getId());
            model.addAttribute("idTask", idTask);
            model.addAttribute("idSupergruppo", idSupergruppo);
            model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));
            model.addAttribute("taskForm", taskForm);

            List<Persona> persone = taskService.getPossibleTaskAssegnees(idSupergruppo);
            model.addAttribute("persone", persone);
            return "task/modifica_task";
        }

    }

    /**
     * Ritorna ad una pagina i task @{@link Task} di una persona @{@link Persona}.
     *
     * @param model per salvare informazioni da recuperare nell'html
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/taskPersonali")
    public String visualizzaListaTaskPersonali(final Model model) {
        List<Task> ris = taskService.visualizzaTaskUser();
        model.addAttribute("listaTask", ris);
        return "task/miei_task";
    }

    /**
     * Ritorna ad una pagina i dettagli di un  task @{@link Task} di una persona @{@link Persona}.
     *
     * @param model  per salvare informazioni da recuperare nell'html
     * @param idTask identifica univocamente un task di cui si vogliono visualizzare i dettagli
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/taskPersonali/task_detail/{idTask}")
    public String visualizzaDettagliTaskPersonali(final Model model, @PathVariable(name = "idTask") final int idTask) {
        Task task = taskService.getTaskById(idTask);
        model.addAttribute("task", task);
        model.addAttribute("documento", task.getDocumento());
        return "task/dettagli_task_personali";
    }

    /**
     * Permette di completare un task @{@link Task} personale.
     *
     * @param model  per salvare informazioni da recuperare nell'html
     * @param idTask identifica univocamente un task che si vuole completare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/taskPersonali/task_detail/{idTask}/completa")
    public String completaTaskPersonale(final Model model, @PathVariable(name = "idTask") final int idTask) {
        model.addAttribute("task", taskService.getTaskById(idTask));
        taskService.completaTask(idTask);
        model.addAttribute("flagAzione", FLAG_COMPLETA);
        return "task/dettagli_task_personali";
    }

    /**
     * Permette di aggiungere un documento @{@link Documento} ad un task @{@link Task}.
     *
     * @param model  per salvare informazioni da recuperare nell'html
     * @param file   da aggiungere a documento
     * @param idTask identifica univocamente un task al cui si vuole aggiungere un documento
     * @return il path della pagina su cui eseguire il redirect
     */
    @PostMapping("/taskPersonali/task_detail/{idTask}/uploadDocumento")
    public String uploadDocumentoTask(final Model model, @RequestParam("file") final MultipartFile file, @PathVariable(name = "idTask") final int idTask) {

        Task task = taskService.getTaskById(idTask);
        if (file.isEmpty()) {
            model.addAttribute("task", task);
            model.addAttribute("flagAggiunta", 0);
            return "task/dettagli_task_personali";
        }
        try {
            task = taskService.attachDocumentToTask(task, file.getOriginalFilename(), file.getBytes(), file.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("flagAggiunta", FLAG_APPROVA); //cambiare
        model.addAttribute("task", task);
        model.addAttribute("documento", task.getDocumento());

        return "task/dettagli_task_personali";
    }

    /**
     * Permette l'inoltro di un documento @{@link Documento} al responsabile PQA.
     *
     * @param model         per salvare informazioni da recuperare nell'html
     * @param idSupergruppo identifica il gruppo a cui appartine il documento da inoltrare
     * @param idTask        identifica il task a cui appartine il documento da inoltrare
     * @return il path della pagina su cui eseguire il redirect
     */
    @GetMapping("/gruppi/{idSupergruppo}/tasks/{idTask}/inoltro")
    public String inoltroPQA(@ModelAttribute final TaskForm taskForm, final Model model,
                             @PathVariable(name = "idSupergruppo") final int idSupergruppo,
                             @PathVariable(name = "idTask") final int idTask) {

        taskService.forwardApprovedTaskToPqa(idTask);
        model.addAttribute("isResponsabile", taskService.currentPersonaCanCreateTask(idSupergruppo));

        model.addAttribute("idSupergruppo", Integer.toString(idSupergruppo));
        model.addAttribute("listaTask", taskService.visualizzaTaskSuperGruppo(idSupergruppo));

        List<Documento> listaDocumenti = taskService.getApprovedDocumentiOfSupergruppo(idSupergruppo);
        model.addAttribute("documenti", listaDocumenti);
        model.addAttribute("flagInoltro", FLAG_APPROVA);

        return "task/tasks_supergruppo";
    }

    /**
     * Ottiene il file relativo ad un documento @{@link Documento} di un task
     *
     * @param idTask task dal quale ottenere il documento
     * @return response di tipo resource contenente il file richiesto
     */
    @GetMapping("/task/{idTask}/attachment")
    public ResponseEntity<Resource> downloadDocumento(@PathVariable("idTask") final int idTask) {

        if (taskService.currentPersonaCanViewTask(idTask)) {
            Task currentTask = taskService.getTaskById(idTask);
            Resource resource = taskService.getResourceFromTask(currentTask);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(currentTask.getDocumento().getFormat()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + currentTask.getDocumento().getNome() + "\"")
                    .body(resource);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }
}

