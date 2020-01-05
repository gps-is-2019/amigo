package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.documento.service.DocumentoService;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @ParameterizedTest
    @MethodSource("provideVisualizzaListaTaskSupergruppo")
    void visualizzaListaTaskSupergruppo(Persona expectedPersona, Supergruppo expectedSupergruppo, Task task, Boolean isResponsible) throws Exception {

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask= new ArrayList<>();
        expectedTask.add(task);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.visualizzaTaskSuperGruppo(expectedSupergruppo.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks", expectedSupergruppo.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", "" + expectedSupergruppo.getId()))
                .andExpect(model().attribute("listaTask", expectedTask))
                .andExpect(view().name("task/tasks_supergruppo"));
    }
    private static Stream<Arguments> provideVisualizzaListaTaskSupergruppo() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(persona1, gruppo1, task1, true),
                Arguments.of(persona2, gruppo2, task2, true),
                Arguments.of(persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDefinizioneTaskSupergruppo")
    void definizioneTaskSupergruppo( User user,  Persona expectedPersona, Supergruppo expectedSupergruppo) throws Exception{
        expectedPersona.setUser(user);
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
                .andExpect(view().name("task/crea_task"));
    }

    private static Stream<Arguments> provideDefinizioneTaskSupergruppo() {

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo supergruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo supergruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        Supergruppo supergruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);

        return Stream.of(
                Arguments.of(user1, persona1, supergruppo1),
                Arguments.of(user2, persona2, supergruppo2),
                Arguments.of(user3, persona3, supergruppo3)
        );
    }

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

    @ParameterizedTest
    @MethodSource("provideVisualizzaDettagliTaskSupergruppo")
    void visualizzaDettagliTaskSupergruppo(Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception{
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }
    private static Stream<Arguments> provideVisualizzaDettagliTaskSupergruppo() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(persona1, gruppo1, task1, true),
                Arguments.of(persona2, gruppo2, task2, true),
                Arguments.of(persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideApprovazioneTask")
    void approvazioneTask(Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception{
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 1;

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }
    private static Stream<Arguments> provideApprovazioneTask() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(persona1, gruppo1, task1, true),
                Arguments.of(persona2, gruppo2, task2, true),
                Arguments.of(persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRifiutoTask")
    void rifiutoTask(Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception{

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 2;

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/rifiuta", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }
    private static Stream<Arguments> provideRifiutoTask() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(persona1, gruppo1, task1, true),
                Arguments.of(persona2, gruppo2, task2, true),
                Arguments.of(persona3, gruppo3, task3, false)
        );
    }


    @ParameterizedTest
    @MethodSource("provideCompletaTask")
    void completaTask(Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception{
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/completa", expectedSupergruppo.getId(), expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(),expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }
    private static Stream<Arguments> provideCompletaTask() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(persona1, gruppo1, task1, true),
                Arguments.of(persona2, gruppo2, task2, true),
                Arguments.of(persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideModificaTask")
    void modificaTask(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task task, Boolean isResponsible) throws Exception{
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
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
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(isResponsible);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modifica", expectedSupergruppo.getId(), task.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskForm", taskForm))
                .andExpect(model().attribute("idTask", task.getId()))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("isResponsabile", isResponsible))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("task/modifica_task"));
    }
    private static Stream<Arguments> provideModificaTask() {
        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true),
                Arguments.of(user2, persona2, gruppo2, task2, true),
                Arguments.of(user3, persona3, gruppo3, task3, false)
        );
    }

    @Test
    void saveModifyTask() {

    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaListaTaskPersonali")
    void visualizzaListaTaskPersonali(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task task) throws Exception{
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
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
                .andExpect(view().name("task/miei_task"));
    }
    private static Stream<Arguments> provideVisualizzaListaTaskPersonali() {
        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaDettagliTaskPersonali")
    void visualizzaDettagliTaskPersonali( User user,Persona expectedPersona, Task expectedTask) throws Exception{
        expectedPersona.setUser(user);
        expectedTask.setPersona(expectedPersona);

        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}", expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_personali"));

    }
    private static Stream<Arguments> provideVisualizzaDettagliTaskPersonali() {
        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");


        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, task1),
                Arguments.of(user2, persona2, task2),
                Arguments.of(user3, persona3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCompletaTaskPersonale")
    void completaTaskPersonale(Persona expectedPersona,Task expectedTask) throws Exception{
        expectedTask.setPersona(expectedPersona);
        int expectedFlag = 4;

        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}/completa", expectedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAzione", expectedFlag))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_personali"));
    }
    private static Stream<Arguments> provideCompletaTaskPersonale() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");


        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" , date1, "task1" , "completo");
        Task task2 = new Task("t1" , date2, "task2" , "incompleto");
        Task task3 = new Task("t1" , date3, "chiamare azienda" , "incompleto");

        return Stream.of(
                Arguments.of(persona1, task1),
                Arguments.of(persona2, task2),
                Arguments.of(persona3, task3)
        );
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

        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);
        when(documentoService.addDocumento(null)).thenReturn(documento);

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
