package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.consegna.domain.Consegna;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("provideVisualizzaListaTaskSupergruppo")
    void visualizzaListaTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task, final Boolean isResponsible) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask = new ArrayList<>();
        expectedTask.add(task);

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.visualizzaTaskSuperGruppo(expectedSupergruppo.getId())).thenReturn(expectedTask);
        when(taskService.getApprovedDocumentiOfSupergruppo(expectedSupergruppo.getId())).thenReturn(null);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", isResponsible))
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
    void definizioneTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        Task task = new Task();

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(true);
        when(taskService.getPossibleTaskAssegnees(expectedSupergruppo.getId())).thenReturn(expectedPersone);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/create", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())

                .andExpect(model().attribute("task", task))
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

    @ParameterizedTest
    @MethodSource("provideSaveTaskPost")
    void saveTaskPost(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, TaskForm taskForm, Task task, Boolean isRespnsabile) throws Exception {
        user.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        expectedPersona.setUser(user);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        task.setPersona(expectedPersona);

        when(taskService.definizioneTaskSupergruppo(task.getDescrizione(), task.getDataScadenza(), task.getNome(), task.getStato(),
                expectedSupergruppo.getId(), expectedPersona.getId())).thenReturn(task);
        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(true);

        this.mockMvc.perform(post("/gruppi/{idSupergruppo}/tasks/create", expectedSupergruppo.getId())
                .with(csrf())
                .sessionAttr("taskForm", taskForm)
                .param("id", "" + taskForm.getId())
                .param("descrizione", taskForm.getDescrizione())
                .param("dataScadenza", taskForm.getDataScadenza())
                .param("nome", taskForm.getNome())
                .param("stato", taskForm.getStato())
                .param("idPersona", "" + taskForm.getIdPersona())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("task", task))
                .andExpect(model().attribute("isResponsabile", isRespnsabile))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("flagCreazione", true))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideSaveTaskPost() {
        LocalDate date1 = LocalDate.of(2026, 12, 30);
        LocalDate date2 = LocalDate.of(2026, 12, 30);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);

        TaskForm taskForm1 = new TaskForm(1, "t1", "2026-12-30", "task2", "incompleto", 0);
        taskForm1.setIdPersona(persona1.getId());
        Task task1 = new Task(taskForm1.getDescrizione(), date1, taskForm1.getNome(), taskForm1.getStato());
        task1.setId(1);
        TaskForm taskForm2 = new TaskForm(2, "t1", "2026-12-30", "task2", "incompleto", 0);
        taskForm2.setIdPersona(persona2.getId());
        Task task2 = new Task(taskForm2.getDescrizione(), date2, taskForm2.getNome(), taskForm2.getStato());
        task2.setId(2);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, taskForm1, task2, true),
                Arguments.of(user2, persona2, gruppo2, taskForm2, task2, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSaveTaskPosterror")
    void saveTaskPostError(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, TaskForm taskForm) throws Exception {
        user.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        expectedPersona.setUser(user);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);

        when(taskService.getPossibleTaskAssegnees(expectedSupergruppo.getId())).thenReturn(expectedPersone);

        this.mockMvc.perform(post("/gruppi/{idSupergruppo}/tasks/create", expectedSupergruppo.getId())
                .sessionAttr("taskForm", taskForm)
                .param("id", "" + taskForm.getId())
                .param("descrizione", taskForm.getDescrizione())
                .param("dataScadenza", taskForm.getDataScadenza())
                .param("nome", taskForm.getNome())
                .param("stato", taskForm.getStato())
                .param("idPersona", "" + taskForm.getIdPersona())
                .with(user(userDetails))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("flagCreazione", false))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("task/crea_task"));
    }

    private static Stream<Arguments> provideSaveTaskPosterror() {
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

        TaskForm taskForm1 = new TaskForm(1, "", "", "", "", 0);
        TaskForm taskForm2 = new TaskForm(2, "$$$$", "2020-12-31", "null", "null", 0);
        TaskForm taskForm3 = new TaskForm(3, "adsdd", "2021-12-12", "$$$", "In valutazione", 0);
        TaskForm taskForm4 = new TaskForm(4, "adsdd", "2020-12-31", "", "", 0);
        TaskForm taskForm5 = new TaskForm(5, "asdasda", "2040-12-12", "", "in valutazione", 0);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, taskForm1),
                Arguments.of(user2, persona2, gruppo2, taskForm2),
                Arguments.of(user3, persona3, gruppo3, taskForm3),
                Arguments.of(user1, persona1, gruppo1, taskForm4),
                Arguments.of(user2, persona2, gruppo2, taskForm5)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaDettagliTaskSupergruppo")
    void visualizzaDettagliTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask, final Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", true))
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
                Arguments.of(user3, persona3, gruppo3, task3, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideApprovazioneTask")
    void approvazioneTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask, final Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 1;

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", isResponsible))
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
    void rifiutoTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask, final Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 2;

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/rifiuta", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", isResponsible))
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
    void completaTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask, final Boolean isResponsible) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(expectedTask.getId())).thenReturn(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/completa", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", isResponsible))
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
    void modificaTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task, final Boolean isResponsible) throws Exception {
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

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(task.getId())).thenReturn(task);
        when(taskService.getPossibleTaskAssegnees(expectedSupergruppo.getId())).thenReturn(expectedPersone);

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

    @ParameterizedTest
    @MethodSource("provideSaveModifyTask")
    void saveModifyTask(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, TaskForm taskForm, Task task, Boolean isResponsabile) throws Exception {
        user.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        expectedPersona.setUser(user);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        task.setPersona(expectedPersona);

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsabile);
        when(taskService.getTaskById(task.getId())).thenReturn(task);

        this.mockMvc.perform(post("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modificaTask", expectedSupergruppo.getId(), task.getId())
                .with(csrf())
                .sessionAttr("taskForm", taskForm)
                .param("id", "" + taskForm.getId())
                .param("descrizione", taskForm.getDescrizione())
                .param("dataScadenza", taskForm.getDataScadenza())
                .param("nome", taskForm.getNome())
                .param("stato", taskForm.getStato())
                .param("idPersona", "" + taskForm.getIdPersona())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", isResponsabile))
                .andExpect(model().attribute("flagAzione", 3))
                .andExpect(model().attribute("task", task))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideSaveModifyTask() {
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

        TaskForm taskForm1 = new TaskForm(1, "t1", "2030-01-30", "task1", "incompleto", 1);
        TaskForm taskForm2 = new TaskForm(2, "t1", "2029-12-30", "task2", "incompleto", 2);
        TaskForm taskForm3 = new TaskForm(3, "t3", "2021-11-03", "task3", "incompleto", 3);


        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, taskForm1, task1, true),
                Arguments.of(user2, persona2, gruppo2, taskForm2, task2, true),
                Arguments.of(user3, persona3, gruppo3, taskForm3, task3, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSaveModifyTaskError")
    void saveModifyTaskError(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task, final Boolean isResponsible) throws Exception {
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

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.getTaskById(task.getId())).thenReturn(task);
        when(taskService.getPossibleTaskAssegnees(expectedSupergruppo.getId())).thenReturn(expectedPersone);

        this.mockMvc.perform(post("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modificaTask", expectedSupergruppo.getId(), task.getId())
                .with(csrf())
                .sessionAttr("taskForm", taskForm)
                .param("id", "" + taskForm.getId())
                .param("descrizione", taskForm.getDescrizione())
                .param("dataScadenza", taskForm.getDataScadenza())
                .param("nome", taskForm.getNome())
                .param("stato", taskForm.getStato())
                .param("idPersona", "" + taskForm.getIdPersona())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskForm", taskForm))
                .andExpect(model().attribute("idTask", task.getId()))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("isResponsabile", isResponsible))
                .andExpect(model().attribute("persone", expectedPersone))
                .andExpect(view().name("task/modifica_task"));
    }

    private static Stream<Arguments> provideSaveModifyTaskError() {
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

        Task task1 = new Task("", date1, "task1", "completo");
        task1.setId(1);
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        task2.setId(2);
        Task task3 = new Task("t1", date3, "", "incompleto");
        task3.setId(3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true),
                Arguments.of(user2, persona2, gruppo2, task2, true),
                Arguments.of(user3, persona3, gruppo3, task3, false)
        );
    }


    @ParameterizedTest
    @MethodSource("provideVisualizzaListaTaskPersonali")
    void visualizzaListaTaskPersonali(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);

        expectedSupergruppo.addPersona(expectedPersona);
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        task.setPersona(expectedPersona);
        expectedPersona.addTask(task);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task);

        when(taskService.visualizzaTaskUser()).thenReturn(expectedTasks);

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
    void visualizzaDettagliTaskPersonali(final User user, final Persona expectedPersona, final Task expectedTask) throws Exception {

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
    void completaTaskPersonale(final User user, final Persona expectedPersona, final Task expectedTask) throws Exception {
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
    @MethodSource("provideUploadDocumentoPost")
    void uploadDocumento(User user, Task task, MockMultipartFile file, int flag) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);

        when(taskService.getTaskById(task.getId())).thenReturn(task);
        when(taskService.attachDocumentToTask(task, file.getOriginalFilename(), file.getBytes(), file.getContentType())).thenReturn(task);

        if (flag == 1) {
            this.mockMvc.perform(multipart("/taskPersonali/task_detail/{idTask}/uploadDocumento", task.getId()).file(file)
                    .with(csrf())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("documento", task.getDocumento()))
                    .andExpect(model().attribute("flagAggiunta", flag))
                    .andExpect(model().attribute("task", task))
                    .andExpect(view().name("task/dettagli_task_personali"));
        } else {
            this.mockMvc.perform(multipart("/taskPersonali/task_detail/{idTask}/uploadDocumento", task.getId()).file(file)
                    .with(csrf())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("flagAggiunta", flag))
                    .andExpect(model().attribute("task", task))
                    .andExpect(view().name("task/dettagli_task_personali"));
        }
    }

    private static Stream<Arguments> provideUploadDocumentoPost() {
        User user = new User("admin", "admin");
        user.addRole(new Role(Role.PQA_ROLE));
        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setId(1);
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        persona2.setId(2);
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        persona3.setId(3);
        Documento documento1 = new Documento();
        documento1.setNome("test.pdf");
        Documento documento2 = new Documento();
        documento2.setNome("test.txt");
        Documento documento3 = new Documento();
        documento3.setNome("test.zip");

        Task task1 = new Task("adsfafsa", LocalDate.now(), "dadeqewdq", "incompleto");
        task1.setPersona(persona1);
        task1.setId(1);
        task1.setDocumento(documento1);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[1]);
        MockMultipartFile file2 = new MockMultipartFile("file", "test.txt", "application/pdf", new byte[1]);
        MockMultipartFile file3 = new MockMultipartFile("file", "test.zip", "application/pdf", new byte[1]);
        MockMultipartFile file4 = new MockMultipartFile("file", "test.rar", "application/pdf", new byte[1]);
        MockMultipartFile file5 = new MockMultipartFile("file", "test.exe", "application/pdf", new byte[1]);
        MockMultipartFile file6 = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);
        MockMultipartFile file7 = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[10485761]);

        return Stream.of(
                Arguments.of(user, task1, file, 1),
                Arguments.of(user, task1, file2, 1),
                Arguments.of(user, task1, file4, 1),
                Arguments.of(user, task1, file3, 1),
                Arguments.of(user, task1, file5, 1),
                Arguments.of(user, task1, file6, 0),
                Arguments.of(user, task1, file7, 1)

        );
    }


    @ParameterizedTest
    @MethodSource("provideInoltroPQA")
    void inoltroPQA(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task, final Boolean isResponsible,
                    final Documento doc, final Consegna consegna) throws Exception {

        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task);
        List<Documento> expectedDocs = new ArrayList<>();
        expectedDocs.add(doc);

        when(taskService.currentPersonaCanCreateTask(expectedSupergruppo.getId())).thenReturn(isResponsible);
        when(taskService.visualizzaTaskSuperGruppo(expectedSupergruppo.getId())).thenReturn(expectedTasks);
        when(taskService.getApprovedDocumentiOfSupergruppo(expectedSupergruppo.getId())).thenReturn(expectedDocs);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/{idTask}/inoltro", expectedSupergruppo.getId(), task.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", isResponsible))
                .andExpect(model().attribute("idSupergruppo", "" + expectedSupergruppo.getId()))
                .andExpect(model().attribute("listaTask", expectedTasks))
                .andExpect(model().attribute("documenti", expectedDocs))
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
        documento1.setTask(task1);
        Documento documento2 = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(),
                "test1.txt", false, "text/plain");
        task2.setDocumento(documento2);
        documento2.setTask(task2);
        Documento documento3 = new Documento("src/main/resources/documents/test2.txt", LocalDate.now(),
                "test2.txt", false, "text/plain");
        task3.setDocumento(documento3);
        documento3.setTask(task3);

        Consegna consegna = new Consegna();

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1, true, documento1, consegna),
                Arguments.of(user2, persona2, gruppo2, task2, true, documento2, consegna),
                Arguments.of(user3, persona3, gruppo3, task3, false, documento3, consegna)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumentoNull(User user, Task task, Documento documento) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");
        when(taskService.currentPersonaCanViewTask(task.getId())).thenReturn(false);

        this.mockMvc.perform(get("/task/{idTask}/attachment", documento.getId())
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("https://i.makeagif.com/media/6-18-2016/i4va3h.gif"));

    }

    private static Stream<Arguments> provideDownloadDocumento() {
        User user1 = new User("ferrucci@unista.it", "ferrucci");
        Documento documento = new Documento();
        Task task = new Task("t1", LocalDate.now(), "task1", "incompleto");
        task.setId(1);
        task.setDocumento(documento);
        documento.setTask(task);
        documento.setPath("/src/test/resources/documents/file.txt");
        documento.setId(100);
        documento.setNome("test.txt");
        documento.setFormat("text/plain");

        return Stream.of(
                Arguments.of(user1, task, documento)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(User user, Task task, Documento documento) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);

        when(taskService.getTaskById(task.getId())).thenReturn(task);
        when(taskService.currentPersonaCanViewTask(task.getId())).thenReturn(true);

        this.mockMvc.perform(get("/task/{idTask}/attachment", task.getId())
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(header().exists("Content-Disposition"));
    }

}
