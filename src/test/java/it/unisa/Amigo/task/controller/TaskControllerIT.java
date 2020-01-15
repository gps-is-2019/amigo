package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.Role;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.documento.dao.DocumentoDAO;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIT {

    @Autowired
    private TaskService taskService;

    @Autowired
    private GruppoService gruppoService;

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private SupergruppoDAO supergruppoDAO;

    @Autowired
    private DocumentoDAO documentoDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void afterEach() {
        taskDAO.deleteAll();
        supergruppoDAO.deleteAll();
        personaDAO.deleteAll();
        userDAO.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaListaTaskSupergruppo")
    void visualizzaListaTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppoResponsabile(expectedSupergruppo);
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask = new ArrayList<>();
        expectedTask.add(task);

        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(task);
        userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", true))
                .andExpect(model().attribute("idSupergruppo", "" + expectedSupergruppo.getId()))
                .andExpect(model().attribute("listaTask", expectedTask))
                .andExpect(view().name("task/tasks_supergruppo"));
    }

    private static Stream<Arguments> provideVisualizzaListaTaskSupergruppo() {
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDefinizioneTaskSupergruppo")
    void definizioneTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        Task task = new Task();

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        userDAO.save(user);

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

    @ParameterizedTest
    @MethodSource("provideSaveTaskPost")
    void saveTaskPost(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, TaskForm taskForm, Task task, Boolean isResponsabile) throws Exception {
        user.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        userDAO.save(user);

        this.mockMvc.perform(post("/gruppi/{idSupergruppo}/tasks/create", expectedSupergruppo.getId())
                .with(csrf())
                .sessionAttr("taskForm", taskForm)
                .param("id", "" + taskForm.getId())
                .param("descrizione", taskForm.getDescrizione())
                .param("dataScadenza", taskForm.getDataScadenza())
                .param("nome", taskForm.getNome())
                .param("stato", taskForm.getStato())
                .param("idPersona", "" + expectedPersona.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", isResponsabile))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("flagCreazione", true))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideSaveTaskPost() {
        LocalDate date1 = LocalDate.of(2055, 12, 30);
        LocalDate date2 = LocalDate.of(2055, 12, 30);

        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        TaskForm taskForm1 = new TaskForm(1, "t1", "2055-12-30", "task2", "incompleto", 1);
        Task task1 = new Task(taskForm1.getDescrizione(), date1, taskForm1.getNome(), taskForm1.getStato());
        TaskForm taskForm2 = new TaskForm(2, "t1", "2055-12-30", "task2", "incompleto", 2);
        Task task2 = new Task(taskForm2.getDescrizione(), date2, taskForm2.getNome(), taskForm2.getStato());

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, taskForm1, task1, true),
                Arguments.of(user2, persona2, gruppo2, taskForm2, task2, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSaveTaskPosterror")
    void saveTaskPostError(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, TaskForm taskForm) throws Exception {
        user.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        userDAO.save(user);

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
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.addPersona(persona1);
        gruppo1.setResponsabile(persona1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.addPersona(persona2);
        gruppo2.setResponsabile(persona2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.addPersona(persona3);
        gruppo3.setResponsabile(persona3);

        TaskForm taskForm1 = new TaskForm(1, "", "", "", "", 0);
        TaskForm taskForm2 = new TaskForm(2, "null", "", "", "null", 0);
        TaskForm taskForm3 = new TaskForm(3, "adsdd", "", "", "", 0);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, taskForm1),
                Arguments.of(user2, persona2, gruppo2, taskForm2),
                Arguments.of(user3, persona3, gruppo3, taskForm3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaDettagliTaskSupergruppo")
    void visualizzaDettagliTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        taskDAO.save(expectedTask);
        // userDAO.save(user);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", expectedTask))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideVisualizzaDettagliTaskSupergruppo() {
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideApprovazioneTask")
    void approvazioneTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);
        int flagAzione = 1;

        personaDAO.save(expectedPersona);
        userDAO.save(user);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(expectedTask);

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/approva", expectedSupergruppo.getId(), expectedTask.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("task", taskService.getTaskById(expectedTask.getId())))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideApprovazioneTask() {
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRifiutoTask")
    void rifiutoTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
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
                .andExpect(model().attribute("task", taskService.getTaskById(expectedTask.getId())))
                .andExpect(model().attribute("flagAzione", flagAzione))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideRifiutoTask() {
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCompletaTask")
    void completaTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
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
                .andExpect(model().attribute("task", taskService.getTaskById(expectedTask.getId())))
                .andExpect(view().name("task/dettagli_task_supergruppo"));
    }

    private static Stream<Arguments> provideCompletaTask() {
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideModificaTask")
    void modificaTask(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        List<Persona> expectedPersone = new ArrayList<>();
        expectedPersone.add(expectedPersona);
        task.setPersona(expectedPersona);
        task.setSupergruppo(expectedSupergruppo);

        // personaDAO.save(expectedPersona);
        // supergruppoDAO.save(expectedSupergruppo);
        // userDAO.save(user);
        taskDAO.save(task);

        TaskForm taskForm = new TaskForm();
        taskForm.setId(task.getId());
        taskForm.setNome(task.getNome());
        taskForm.setDataScadenza(task.getDataScadenza().toString().substring(0, 10));
        taskForm.setDescrizione(task.getDescrizione());
        taskForm.setStato(task.getStato());
        taskForm.setIdPersona(expectedPersona.getId());


        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modifica", expectedSupergruppo.getId(), task.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("taskForm", taskForm))
                .andExpect(model().attribute("idTask", task.getId()))
                .andExpect(model().attribute("idSupergruppo", expectedSupergruppo.getId()))
                .andExpect(model().attribute("isResponsabile", true))
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }


    @ParameterizedTest
    @MethodSource("provideSaveModifyTask")
    void saveModifyTask(User user, Persona expectedPersona, Supergruppo expectedSupergruppo, TaskForm taskForm, Task task, Boolean isResponsabile) throws Exception {
        user.addRole(new Role(Role.CAPOGRUPPO_ROLE));
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(task);
        userDAO.save(user);

        this.mockMvc.perform(post("/gruppi/{idSupergruppo}/tasks/task_detail/{idTask}/modificaTask", expectedSupergruppo.getId(), task.getId())
                .with(csrf())
                .sessionAttr("taskForm", taskForm)
                .param("id", "" + task.getId())
                .param("descrizione", taskForm.getDescrizione())
                .param("dataScadenza", taskForm.getDataScadenza())
                .param("nome", taskForm.getNome())
                .param("stato", taskForm.getStato())
                .param("idPersona", "" + expectedPersona.getId())
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

        LocalDate date1 = LocalDate.of(2030, 1, 30);
        LocalDate date2 = LocalDate.of(2029, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 11, 3);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("Roberto", "De Prisco", "user");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);

        TaskForm taskForm1 = new TaskForm(1, "t1", "2030-01-30", "task1", "incompleto", 1);
        TaskForm taskForm2 = new TaskForm(2, "t1", "2029-12-30", "task2", "incompleto", 2);
        TaskForm taskForm3 = new TaskForm(3, "t3", "2021-11-03", "task3", "incompleto", 3);


        Task task1 = new Task("t1", date1, "task1", "incompleto");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t3", date3, "task3", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, taskForm1, task1, true),
                Arguments.of(user2, persona2, gruppo2, taskForm2, task2, true),
                Arguments.of(user3, persona3, gruppo3, taskForm3, task3, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaListaTaskPersonali")
    void visualizzaListaTaskPersonali(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaDettagliTaskPersonali")
    void visualizzaDettagliTaskPersonali(final User user, final Supergruppo expectedSupergruppo, final Persona expectedPersona, final Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
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
                .andExpect(view().name("task/dettagli_task_personali"));

    }

    private static Stream<Arguments> provideVisualizzaDettagliTaskPersonali() {
        User user1 = new User("admin", "admin");
        User user2 = new User("rob@deprisco.it", "roberto");
        User user3 = new User("vittorio@scarano.it", "scarano");

        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo gruppo2 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo gruppo3 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");


        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, gruppo1, persona1, task1),
                Arguments.of(user2, gruppo2, persona2, task2),
                Arguments.of(user3, gruppo3, persona3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCompletaTaskPersonale")
    void completaTaskPersonale(final User user, final Supergruppo expectedSupergruppo, final Persona expectedPersona, final Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
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
                .andExpect(model().attribute("task", taskService.getTaskById(expectedTask.getId())))
                .andExpect(view().name("task/dettagli_task_personali"));
    }

    private static Stream<Arguments> provideCompletaTaskPersonale() {
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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(user1, gruppo1, persona1, task1),
                Arguments.of(user2, gruppo2, persona2, task2),
                Arguments.of(user3, gruppo3, persona3, task3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInoltroPQA")
    void inoltroPQA(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task task) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        user.setPersona(expectedPersona);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedPersona.addSupergruppoResponsabile(expectedSupergruppo);
        task.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(task);
        List<Task> expectedTask = new ArrayList<>();
        expectedTask.add(task);

        personaDAO.save(expectedPersona);
        supergruppoDAO.save(expectedSupergruppo);
        taskDAO.save(task);
        userDAO.save(user);

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

        Task task1 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task2 = new Task("t1", date2, "task2", "incompleto");
        Task task3 = new Task("t1", date3, "chiamare azienda", "incompleto");


        Documento documento1 = new Documento("src/main/resources/documents/test.txt", LocalDate.now(),
                "test.txt", false, "text/plain");
        task1.setDocumento(documento1);
        Documento documento2 = new Documento("src/main/resources/documents/test1.txt", LocalDate.now(),
                "test1.txt", false, "text/plain");
        task2.setDocumento(documento2);
        Documento documento3 = new Documento("src/main/resources/documents/test2.txt", LocalDate.now(),
                "test2.txt", false, "text/plain");
        task3.setDocumento(documento3);

        return Stream.of(
                Arguments.of(user1, persona1, gruppo1, task1),
                Arguments.of(user2, persona2, gruppo2, task2),
                Arguments.of(user3, persona3, gruppo3, task3)
        );
    }

@ParameterizedTest
    @MethodSource("provideUploadDocumentoPost")
    void uploadDocumento(User user, Task task, MockMultipartFile file, int flag, String nameModel, String contentModel) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);

        userDAO.save(user);
        taskDAO.save(task);

        if (flag == 1) {
            this.mockMvc.perform(multipart("/taskPersonali/task_detail/{idTask}/uploadDocumento", task.getId()).file(file)
                    .with(csrf())
                    .with(user(userDetails)))
                    .andExpect(status().isOk())
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
        Task task1 = new Task("adsfafsa", LocalDate.now(), "dadeqewdq", "incompleto");
        task1.setPersona(persona1);
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[1]);

        return Stream.of(
                Arguments.of(user, task1, file, 1, "documentoNome", file.getOriginalFilename())
        );
    }


    @ParameterizedTest
    @MethodSource("provideDownloadDocumento")
    void downloadDocumento(User user, Task task, Documento documento) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        user.addRole(new Role(Role.PQA_ROLE));

        taskDAO.save(task);
        documentoDAO.save(documento);
        userDAO.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "https://i.makeagif.com/media/6-18-2016/i4va3h.gif");

        this.mockMvc.perform(get("/task/{idTask}/attachment", task.getId())
                .with(csrf())
                .with(user(userDetails)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("https://i.makeagif.com/media/6-18-2016/i4va3h.gif"));

    }

    @SneakyThrows
    private static Stream<Arguments> provideDownloadDocumento() {
        User user1 = new User("ferrucci@unista.it", "ferrucci");
        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        persona1.setUser(user1);
        user1.setPersona(persona1);

        Supergruppo supergruppo = new Supergruppo("QAQD", "Commissione", true);
        Task task = new Task("t1", LocalDate.now(), "task1", "incompleto");
        task.setPersona(persona1);
        persona1.addTask(task);
        task.setSupergruppo(supergruppo);
        supergruppo.addTask(task);

        Documento documento = new Documento();
        documento.setPath("src/test/resources/documents/file.txt");
        documento.setNome("test.txt");
        documento.setFormat("text/plain");
        File file = new File("src/test/resources/documents/file.txt");
        file.createNewFile();

        task.setDocumento(documento);
        documento.setTask(task);

        return Stream.of(
                Arguments.of(user1, task, documento)
        );
    }


}
