package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domanin.Role;
import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.consegna.services.ConsegnaService;
import it.unisa.Amigo.documento.domain.Documento;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    private ConsegnaService consegnaService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("provideVisualizzaListaTaskSupergruppo")
    void visualizzaListaTaskSupergruppo(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task task, Boolean isResponsible) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask = new ArrayList<>();
        expectedTask.add(task);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.visualizzaTaskSuperGruppo(expectedSupergruppo.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", "" + expectedSupergruppo.getId()))
                .andExpect(model().attribute("listaTask", expectedTask))
                .andExpect(view().name("task/tasks_supergruppo"));
    }

    private static Stream<Arguments> provideVisualizzaListaTaskSupergruppo() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true),
                Arguments.of(user2, persona2, gruppo2, task2, true),
                Arguments.of(user3, persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDefinizioneTaskSupergruppo")
    void definizioneTaskSupergruppo(User user, Persona expectedPersona, Supergruppo expectedSupergruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        Task task = new Task();

        when(gruppoService.findAllMembriInSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedPersone);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/create", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskForm", task))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("task/crea_task"));
    }

    private static Stream<Arguments> provideDefinizioneTaskSupergruppo() {
        User user1 = new User("admin", "admin");
        user1.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        User user2 = new User("rob@deprisco.it", "roberto");
        user2.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        User user3 = new User("vittorio@scarano.it", "scarano");
        user3.addRole(new Role(Role.CAPOGRUPPO_ROLE));

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        return Stream.of(
                Arguments.of(user2, persona2, gruppo2),
                Arguments.of(user1, persona1, gruppo1),
                Arguments.of(user3, persona3, gruppo3)
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
    void visualizzaDettagliTaskSupergruppo(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideVisualizzaDettagliTaskSupergruppo() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true),
                Arguments.of(user2, persona2, gruppo2, task2, true),
                Arguments.of(user3, persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideApprovazioneTask")
    void approvazioneTask(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 1;

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideApprovazioneTask() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true),
                Arguments.of(user2, persona2, gruppo2, task2, true),
                Arguments.of(user3, persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRifiutoTask")
    void rifiutoTask(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 2;

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/rifiuta", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideRifiutoTask() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true),
                Arguments.of(user2, persona2, gruppo2, task2, true),
                Arguments.of(user3, persona3, gruppo3, task3, false)
        );
    }


    @ParameterizedTest
    @MethodSource("provideCompletaTask")
    void completaTask(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task expectedTask, Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/completa", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideCompletaTask() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);


        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true),
                Arguments.of(user2, persona2, gruppo2, task2, true),
                Arguments.of(user3, persona3, gruppo3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideModificaTask")
    void modificaTask(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task task, Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
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

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modifica", expectedSupergruppo.getId(), task.getId())
                .with(user(userDetails)))
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
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

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
    void visualizzaListaTaskPersonali(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task task) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        task.setPersona(expectedPersona);
        expectedPersona.addTask(task);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(taskService.visualizzaTaskUser(expectedPersona.getId())).thenReturn(expectedTasks);

        this.mockMvc.perform(get("/taskPersonali")
                .with(user(userDetails)))
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
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaDettagliTaskPersonali")
    void visualizzaDettagliTaskPersonali(User user, Persona expectedPersona, Task expectedTask) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedTask.setPersona(expectedPersona);

        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}", expectedTask.getId())
                .with(user(userDetails)))
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
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, task1),
                Arguments.of(user2, persona2, task2),
                Arguments.of(user3, persona3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCompletaTaskPersonale")
    void completaTaskPersonale(User user, Persona expectedPersona, Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedTask.setPersona(expectedPersona);
        int expectedFlag = 4;

        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/taskPersonali/task_detail/{idTask}/completa", expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagAzione", expectedFlag))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_personali"));
    }

    private static Stream<Arguments> provideCompletaTaskPersonale() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, task1),
                Arguments.of(user2, persona2, task2),
                Arguments.of(user3, persona3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInoltroPQA")
    void inoltroPQA(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, Task task, Boolean isResponsible, Documento doc, Consegna consegna) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask = new ArrayList<>();
        expectedTask.add(task);

        when(gruppoService.getAuthenticatedUser()).thenReturn(expectedPersona);
        when(taskService.getTaskById(task.getId())).thenReturn(task);
        when(consegnaService.inoltraPQAfromGruppo(doc)).thenReturn(consegna);
        when(gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.visualizzaTaskSuperGruppo(expectedSupergruppo.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/{idTask}/inoltro", expectedSupergruppo.getId(), task.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", "" + expectedSupergruppo.getId()))
                .andExpect(model().attribute("listaTask", expectedTask))
                .andExpect(model().attribute("flagInoltro", 1))
                .andExpect(view().name("task/tasks_supergruppo"));
    }

    private static Stream<Arguments> provideInoltroPQA() {
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        Documento documento1 = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        task1.setDocumento(documento1);
        Documento documento2 = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(),
                "test1.txt", false, "text/plain");
        task2.setDocumento(documento2);
        Documento documento3 = new Documento("src/main/resources/documents/test2.txt", LocalDate.now(),
                "test2.txt", false, "text/plain");
        task3.setDocumento(documento3);

        Consegna consegna = new Consegna();

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true, documento1, consegna),
                Arguments.of(user2, persona2, gruppo2, task2, true, documento2, consegna),
                Arguments.of(user3, persona3, gruppo3, task3, false, documento3, consegna)
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
