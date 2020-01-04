package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.service.DocumentoService;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = WebMvcAutoConfiguration.class)
//@WebMvcTest(GruppoController.class)
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIT {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private GruppoService gruppoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private SupergruppoDAO supergruppoDAO;

    @Autowired
    private UserDAO userDAO;

    @Test
    void visualizzaListaTaskSupergruppo() throws Exception {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppoResponsabile(expectedSupergruppo);
        Task task = new Task("t1", tmpDate, "task1", "incompleto");
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask = new ArrayList<>();
        expectedTask.add(task);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(task);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", "" + expectedSupergruppo.getId()))
                .andExpect(model().attribute("listaTask", expectedTask))
                .andExpect(view().name("task/paginaVisualizzaListaTaskSupergruppo"));
    }

    @Test
    void definizioneTaskSupergruppo() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        Task task = new Task();

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/creaTask", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskForm", task))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("task/paginaDefinizioneTaskSupergruppo"));
    }

    //TODO
    @Test
    void saveTaskPost() {
//        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
//        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
//        expectedSupergruppo.addPersona(expectedPersona);
//        Task task = new Task("t1" , new Date(), "task1" , "incompleto");
//        task.setSupergruppo(expectedSupergruppo);
//        expectedSupergruppo.addTask(task);
//        task.setPersona(expectedPersona);
//        List<Persona> expectedPersone = new ArrayList<>();
//        expectedPersone.add(expectedPersona);
//
//        this.mockMvc.perform(post("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/creazioneTaskSupergruppo", expectedSupergruppo.getId())
//                )
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("flagCreazione", true))
//                .andExpect(model().attribute("persone", expectedPersone))
//                .andExpect(view().name("task/paginaDefinizioneTaskSupergruppo"));
    }

    @Test
    void visualizzaDettagliTaskSupergruppo() throws Exception {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        Task expectedTask = new Task("t1", tmpDate, "task1", "incompleto");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(expectedTask);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void approvazioneTask() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1", tmpDate, "task1", "approvato");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 1;

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(expectedTask);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void rifiutoTask() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1", tmpDate, "task1", "respinto");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 2;

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(expectedTask);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/rifiuta", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void completaTask() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1", tmpDate, "task1", "in valutazione");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(expectedTask);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/completa", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void modificaTask() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "incompleto");
        task.setPersona(expectedPersona);
        task.setSupergruppo(expectedSupergruppo);

        taskDAO.save(task);

        TaskForm taskForm = new TaskForm();
        taskForm.setId(task.getId());
        taskForm.setNome(task.getNome());
        taskForm.setDataScadenza(task.getDataScadenza().toString().substring(0, 10));
        taskForm.setDescrizione(task.getDescrizione());
        taskForm.setStato(task.getStato());
        taskForm.setIdPersona(expectedPersona.getId());

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modifica", expectedSupergruppo.getId(), task.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskForm", taskForm))
                .andExpect(model().attribute("idTask", task.getId()))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("task/paginaModificaTask"));
    }

    @Test
    void saveModifyTask() {

    }

    @Test
    void visualizzaListaTaskPersonali() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "incompleto");
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        task.setPersona(expectedPersona);
        expectedPersona.addTask(task);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(task);
        userDAO.save(user);

        this.mockMvc.perform(get("/taskPersonali")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listaTask", expectedTasks))
                .andExpect(view().name("task/paginaVisualizzaListaTaskPersonali"));
    }

    @Test
    void visualizzaDettagliTaskPersonali() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1", tmpDate, "task1", "incompleto");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedTask.setPersona(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);

        personaDAO.save(expectedPersona);
        taskDAO.save(expectedTask);
        userDAO.save(user);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}", expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskPersonali"));

    }

    @Test
    void completaTaskPersonale() throws Exception {
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1", tmpDate, "task1", "in valutazione");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedTask.setPersona(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        int expectedFlag = 4;

        personaDAO.save(expectedPersona);
        taskDAO.save(expectedTask);
        userDAO.save(user);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}/completa", expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAzione", expectedFlag))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskPersonali"));
    }
/*
    @Test
    void uploadDocumentoTask() throws Exception {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1", tmpDate, "task1", "incompleto");
        User user = new User("admin", "admin");
        UserDetailImpl userDetails = new UserDetailImpl(user);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        expectedTask.setPersona(expectedPersona);
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        expectedTask.setDocumento(documento);
        documento.setTask(expectedTask);

        personaDAO.save(expectedPersona);
        taskDAO.save(expectedTask);
        userDAO.save(user);

        this.mockMvc.perform(post("/taskPersonali/task_detail/{idTask}/uploadDocumento", expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAggiunta", 1))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("documento", documento))
                .andExpect(view().name("task/paginaDettagliTaskPersonali"));
    }
    */
}
