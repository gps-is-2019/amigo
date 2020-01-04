package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @MockBean
    private TaskService taskService;

    @MockBean
    private GruppoService gruppoService;

    @MockBean
    private DocumentoService documentoService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void visualizzaListaTaskSupergruppo() throws Exception {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        Task task = new Task("t1" , tmpDate, "task1" , "incompleto");
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask= new ArrayList<>();
        expectedTask.add(task);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when(taskService.visualizzaTaskSuperGruppo(expectedSupergruppo.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks", expectedSupergruppo.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", "" + expectedSupergruppo.getId()))
                .andExpect(model().attribute("listaTask", expectedTask))
                .andExpect(view().name("task/paginaVisualizzaListaTaskSupergruppo"));
    }

    @Test
    void definizioneTaskSupergruppo() throws Exception{
        User user = new User("admin", "admin");
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        Task task = new Task();

        when(gruppoService.findAllMembriInSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/creaTask", expectedSupergruppo.getId()))
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
//        when(gruppoService.visualizzaListaMembriSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);
//        when(taskService.definizioneTaskSupergruppo(task.getDescrizione(), task.getDataScadenza(), task.getNome(), task.getStato(), expectedSupergruppo, expectedPersona)).thenReturn(true);
//
//        this.mockMvc.perform(post("/gruppo/visualizzaListaTaskSupergruppo/{idSupergruppo}/creazioneTaskSupergruppo", expectedSupergruppo.getId())
//                )
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("flagCreazione", true))
//                .andExpect(model().attribute("persone", expectedPersone))
//                .andExpect(view().name("task/paginaDefinizioneTaskSupergruppo"));
    }

    @Test
    void visualizzaDettagliTaskSupergruppo() throws Exception{
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        Task expectedTask = new Task("t1" , tmpDate, "task1" , "incompleto");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void approvazioneTask() throws Exception{
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1" , tmpDate, "task1" , "incompleto");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 1;

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void rifiutoTask() throws Exception{
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1" , tmpDate, "task1" , "incompleto");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 2;

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/rifiuta", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void completaTask() throws Exception{
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1" , tmpDate, "task1" , "incompleto");
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(true);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/completa", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskSupergruppo"));
    }

    @Test
    void modificaTask() throws Exception{
        User user = new User("admin", "admin");
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1" , tmpDate, "task1" , "incompleto");
        task.setPersona(expectedPersona);
        task.setSupergruppo(expectedSupergruppo);
        TaskForm taskForm = new TaskForm();
        taskForm.setId(task.getId());
        taskForm.setNome(task.getNome());
        taskForm.setDataScadenza(task.getDataScadenza().toString().substring(0, 10));
        taskForm.setDescrizione(task.getDescrizione());
        taskForm.setStato(task.getStato());
        taskForm.setIdPersona(expectedPersona.getId());

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(taskService.getTaskById(task.getId())).thenReturn(task);
        when(gruppoService.findAllMembriInSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(true);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modifica", expectedSupergruppo.getId(), task.getId()))
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
    void visualizzaListaTaskPersonali() throws Exception{
        User user = new User("admin", "admin");
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        Supergruppo expectedSupergruppo = new Supergruppo("GAQD- Informatica", "gruppo", true);
        expectedSupergruppo.addPersona(expectedPersona);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1" , tmpDate, "task1" , "incompleto");
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        task.setPersona(expectedPersona);
        expectedPersona.addTask(task);
        List<Task> expectedTasks= new ArrayList<>();
        expectedTasks.add(task);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(taskService.visualizzaTaskUser(expectedPersona.getId())).thenReturn(expectedTasks);

        this.mockMvc.perform(get("/taskPersonali"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listaTask", expectedTasks))
                .andExpect(view().name("task/paginaVisualizzaListaTaskPersonali"));
    }

    @Test
    void visualizzaDettagliTaskPersonali() throws Exception{
        User user = new User("admin", "admin");
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedPersona.setUser(user);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1" , tmpDate, "task1" , "incompleto");
        expectedTask.setPersona(expectedPersona);

        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}", expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskPersonali"));

    }

    @Test
    void completaTaskPersonale() throws Exception{
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1" , tmpDate, "task1" , "incompleto");
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedTask.setPersona(expectedPersona);
        int expectedFlag = 1;

        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}/completa", expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAzione", expectedFlag))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/paginaDettagliTaskPersonali"));
    }

    @Test
    void uploadDocumentoTask() throws Exception {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expectedTask = new Task("t1", tmpDate, "task1", "incompleto");
        Persona expectedPersona = new Persona("Admin", "Admin", "Administrator");
        expectedTask.setPersona(expectedPersona);
        Documento documento = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        expectedTask.setDocumento(documento);
        documento.setTask(expectedTask);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);
        when(documentoService.addDocumento(null)).thenReturn(documento);

        this.mockMvc.perform(post("/taskPersonali/task_detail/{idTask}/uploadDocumento", expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAggiunta", 1))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("documento", documento))
                .andExpect(view().name("task/paginaDettagliTaskPersonali"));
    }
}
