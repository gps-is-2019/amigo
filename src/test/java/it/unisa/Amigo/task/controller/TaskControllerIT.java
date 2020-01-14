package it.unisa.Amigo.task.controller;

import it.unisa.Amigo.autenticazione.configuration.UserDetailImpl;
import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.User;
import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoService;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import it.unisa.Amigo.task.services.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        this.mockMvc.perform(get("/gruppi/{idSupergruppo}/tasks", expectedSupergruppo.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isResponsabile", gruppoService.isResponsabile(expectedPersona.getId(), expectedSupergruppo.getId())))
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

    /*
    @ParameterizedTest
    @MethodSource("provideDefinizioneTaskSupergruppo")
    void definizioneTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
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
     */

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

    @ParameterizedTest
    @MethodSource("provideVisualizzaDettagliTaskSupergruppo")
    void visualizzaDettagliTaskSupergruppo(final User user, final Persona expectedPersona, final Supergruppo expectedSupergruppo, final Task expectedTask) throws Exception {
        UserDetailImpl userDetails = new UserDetailImpl(user);
        expectedPersona.setUser(user);
        expectedSupergruppo.addPersona(expectedPersona);
        expectedSupergruppo.setResponsabile(expectedPersona);
        expectedTask.setSupergruppo(expectedSupergruppo);
        expectedSupergruppo.addTask(expectedTask);
        expectedTask.setPersona(expectedPersona);

        // personaDAO.save(expectedPersona);
        // supergruppoDAO.save(expectedSupergruppo);
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

    @Test
    void saveModifyTask() {

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
