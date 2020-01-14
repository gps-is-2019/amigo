package it.unisa.Amigo.task.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.documento.service.DocumentoServiceImpl;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.gruppo.services.GruppoServiceImpl;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@SpringBootTest
class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private GruppoServiceImpl gruppoService;

    @Mock
    private DocumentoServiceImpl documentoService;

    @Mock
    private TaskDAO taskDAO;

    @AfterEach
    void afterEach() {
        taskDAO.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideGetAssegnatarioTask")
    void getAssegnatarioTask(final Persona persona, final Task task) {
        when(taskDAO.findById(task.getId())).thenReturn(Optional.of(task));
        assertEquals(persona, taskService.getAssegnatarioTask(task.getId()));
    }

    private static Stream<Arguments> provideGetAssegnatarioTask() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "2", "PQA");

        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Persona persona4 = new Persona("Admin", "Admin", "Administrator");
        Persona persona5 = new Persona("giovanni", "magi", "Administrator");
        Persona persona6 = new Persona("Vittorio", "Scarano", "user");

        Task task = new Task("t1", tmpDate, "task1", "in valutazione");
        Task task2 = new Task("t2", tmpDate, "task2", "approvato");
        Task task3 = new Task("t3", tmpDate, "task3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task.setPersona(persona1);
        task2.setPersona(persona1);
        task3.setPersona(persona2);
        task4.setPersona(persona4);
        task5.setPersona(persona5);
        task6.setPersona(persona6);
        return Stream.of(
                Arguments.of(persona1, task),
                Arguments.of(persona1, task2),
                Arguments.of(persona2, task3),
                Arguments.of(persona4, task4),
                Arguments.of(persona5, task5),
                Arguments.of(persona6, task6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDefinizioneTaskSupergruppo")
    void definizioneTaskSupergruppo(final String descrizione, final LocalDate dataScadenza, final String nome, final String stato,
                                    final Supergruppo s, final Persona persona, final Task expectedTask) {

        when(gruppoService.findSupergruppo(s.getId())).thenReturn(s);
        when(gruppoService.findPersona(persona.getId())).thenReturn(persona);

        Task actual = taskService.definizioneTaskSupergruppo(descrizione, dataScadenza, nome, stato, s.getId(), persona.getId());
        assertEquals(expectedTask, actual);
    }

    private static Stream<Arguments> provideDefinizioneTaskSupergruppo() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2069, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "2", "PQA");
        Persona persona3 = new Persona("Persona3", "3", "DePrisco");
        Persona persona4 = new Persona("Admin", "Admin", "Administrator");
        Persona persona5 = new Persona("giovanni", "magi", "Administrator");
        Persona persona6 = new Persona("Vittorio", "Scarano", "user");
        Supergruppo supergruppo1 = new Supergruppo("GAQD Informatica", "gruppo", true);
        Supergruppo supergruppo2 = new Supergruppo("Robe", "commisione", false);
        Supergruppo supergruppo3 = new Supergruppo("Boh", "gruppo", true);
        Supergruppo supergruppo4 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo supergruppo5 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo supergruppo6 = new Supergruppo("GAQR- Informatica", "gruppo", true);

        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        task1.setPersona(persona1);
        task1.setSupergruppo(supergruppo1);
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        task2.setPersona(persona2);
        task2.setSupergruppo(supergruppo2);
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        task3.setPersona(persona3);
        task3.setSupergruppo(supergruppo3);
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task4.setPersona(persona4);
        task4.setSupergruppo(supergruppo4);
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        task5.setPersona(persona5);
        task5.setSupergruppo(supergruppo5);
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task6.setPersona(persona6);
        task6.setSupergruppo(supergruppo6);
        return Stream.of(
                Arguments.of("descrizione 1", tmpDate, "Task 1", "incompleto", supergruppo1, persona1, task1),
                Arguments.of("descrizione 2", tmpDate2, "Task 2", "in valutazione", supergruppo2, persona2, task2),
                Arguments.of("descrizione 3", tmpDate3, "Task 3", "respinto", supergruppo3, persona3, task3),
                Arguments.of("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo", supergruppo4, persona4, task4),
                Arguments.of("t1", date2, "task2", "incompleto", supergruppo5, persona5, task5),
                Arguments.of("t1", date3, "chiamare azienda", "incompleto", supergruppo6, persona6, task6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaTaskUser")
    void visualizzaTaskUser(final Persona persona, final List<Task> expectedTasks) {
        when(taskDAO.findAllByPersona_Id(persona.getId())).thenReturn(expectedTasks);
        when(gruppoService.getCurrentPersona()).thenReturn(persona);

        assertEquals(expectedTasks, taskService.visualizzaTaskUser());
    }

    private static Stream<Arguments> provideVisualizzaTaskUser() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        persona1.setId(1);
        Persona persona2 = new Persona("Persona2", "2", "PQA");
        persona2.setId(2);
        Persona persona3 = new Persona("Persona3", "3", "DePrisco");
        persona3.setId(3);
        Persona persona4 = new Persona("Admin", "Admin", "Administrator");
        persona4.setId(4);
        Persona persona5 = new Persona("giovanni", "magi", "Administrator");
        persona5.setId(5);
        Persona persona6 = new Persona("Vittorio", "Scarano", "user");
        persona6.setId(6);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        task1.setPersona(persona1);
        task2.setPersona(persona1);
        task3.setPersona(persona1);
        task1.setPersona(persona2);
        task1.setPersona(persona4);
        task3.setPersona(persona4);
        task1.setPersona(persona5);
        task2.setPersona(persona5);
        task3.setPersona(persona6);
        task2.setPersona(persona6);
        ArrayList<Task> persona1Tasks = new ArrayList<>();
        persona1Tasks.add(task1);
        persona1Tasks.add(task2);
        persona1Tasks.add(task3);
        ArrayList<Task> persona2Tasks = new ArrayList<>();
        persona2Tasks.add(task1);
        ArrayList<Task> persona3Tasks = new ArrayList<>();
        ArrayList<Task> persona4Tasks = new ArrayList<>();
        persona4Tasks.add(task1);
        persona4Tasks.add(task3);
        ArrayList<Task> persona5Tasks = new ArrayList<>();
        persona5Tasks.add(task1);
        persona5Tasks.add(task2);
        ArrayList<Task> persona6Tasks = new ArrayList<>();
        persona6Tasks.add(task3);
        persona6Tasks.add(task2);

        return Stream.of(
                Arguments.of(persona1, persona1Tasks),
                Arguments.of(persona2, persona2Tasks),
                Arguments.of(persona3, persona3Tasks),
                Arguments.of(persona4, persona4Tasks),
                Arguments.of(persona5, persona5Tasks),
                Arguments.of(persona6, persona6Tasks)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVisualizzaTaskSuperGruppo")
    void visualizzaTaskSuperGruppo(final Supergruppo supergruppo, final List<Task> expectedTasks) {
        when(taskDAO.findAllBySupergruppo_Id(supergruppo.getId())).thenReturn(expectedTasks);

        assertEquals(expectedTasks, taskService.visualizzaTaskSuperGruppo(supergruppo.getId()));
    }

    private static Stream<Arguments> provideVisualizzaTaskSuperGruppo() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Supergruppo supergruppo1 = new Supergruppo("GAQD Informatica", "gruppo", true);
        supergruppo1.setId(1);
        Supergruppo supergruppo2 = new Supergruppo("Robe", "commisione", false);
        supergruppo2.setId(2);
        Supergruppo supergruppo3 = new Supergruppo("Boh", "gruppo", true);
        supergruppo3.setId(3);
        Supergruppo supergruppo4 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        supergruppo4.setId(4);
        Supergruppo supergruppo5 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        supergruppo5.setId(5);
        Supergruppo supergruppo6 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        supergruppo6.setId(6);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task4.setSupergruppo(supergruppo4);
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        task5.setSupergruppo(supergruppo5);
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task6.setSupergruppo((supergruppo6));
        task5.setSupergruppo(supergruppo6);
        task1.setSupergruppo(supergruppo1);
        task2.setSupergruppo(supergruppo1);
        task3.setSupergruppo(supergruppo1);
        task1.setSupergruppo(supergruppo2);
        ArrayList<Task> supergruppo1Tasks = new ArrayList<>();
        supergruppo1Tasks.add(task1);
        supergruppo1Tasks.add(task2);
        supergruppo1Tasks.add(task3);
        ArrayList<Task> supergruppo2Tasks = new ArrayList<>();
        supergruppo2Tasks.add(task1);
        ArrayList<Task> supergruppo3Tasks = new ArrayList<>();
        ArrayList<Task> supergruppo4Tasks = new ArrayList<>();
        supergruppo4Tasks.add(task4);
        ArrayList<Task> supergruppo5Tasks = new ArrayList<>();
        supergruppo5Tasks.add(task5);
        ArrayList<Task> supergruppo6Tasks = new ArrayList<>();
        supergruppo6Tasks.add(task6);

        return Stream.of(
                Arguments.of(supergruppo1, supergruppo1Tasks),
                Arguments.of(supergruppo2, supergruppo2Tasks),
                Arguments.of(supergruppo3, supergruppo3Tasks),
                Arguments.of(supergruppo4, supergruppo4Tasks),
                Arguments.of(supergruppo5, supergruppo5Tasks),
                Arguments.of(supergruppo6, supergruppo6Tasks)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetTaskById")
    void getTaskById(final Task task) {
        when(taskDAO.findById(task.getId())).thenReturn(Optional.of(task));
        assertEquals(task, taskService.getTaskById(task.getId()));
    }

    private static Stream<Arguments> provideGetTaskById() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(task1),
                Arguments.of(task2),
                Arguments.of(task3),
                Arguments.of(task4),
                Arguments.of(task5),
                Arguments.of(task6)
        );
    }


    @ParameterizedTest
    @MethodSource("provideAccettazioneTask")
    void accettazioneTask(final Task task) {
        when(taskDAO.findById(task.getId())).thenReturn(Optional.of(task));
        taskService.accettazioneTask(task.getId());
        String expectedStato = "approvato";
        assertEquals(expectedStato, task.getStato());
    }

    private static Stream<Arguments> provideAccettazioneTask() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(task1),
                Arguments.of(task2),
                Arguments.of(task3),
                Arguments.of(task4),
                Arguments.of(task5),
                Arguments.of(task6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideRifiutoTask")
    void rifiutoTask(final Task task) {
        when(taskDAO.findById(task.getId())).thenReturn(Optional.of(task));
        taskService.rifiutoTask(task.getId());

        String expectedStato = "respinto";
        assertEquals(expectedStato, task.getStato());
    }

    private static Stream<Arguments> provideRifiutoTask() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(task1),
                Arguments.of(task2),
                Arguments.of(task3),
                Arguments.of(task4),
                Arguments.of(task5),
                Arguments.of(task6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCompletaTask")
    void completaTask(final Task task) {
        when(taskDAO.findById(task.getId())).thenReturn(Optional.of(task));
        taskService.completaTask(task.getId());

        String expectedStato = "in valutazione";
        assertEquals(expectedStato, task.getStato());
    }

    private static Stream<Arguments> provideCompletaTask() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        return Stream.of(
                Arguments.of(task1),
                Arguments.of(task2),
                Arguments.of(task3),
                Arguments.of(task4),
                Arguments.of(task5),
                Arguments.of(task6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideAttachDocumentToTask")
    void attachDocumentToTask(final Task task, Documento doc) {
        when(documentoService.addDocumento(doc.getNome(), doc.getNome().getBytes(), doc.getFormat())).thenReturn(doc);
        when(taskDAO.save(task)).thenReturn(task);

        taskService.attachDocumentToTask(task, doc.getNome(), doc.getNome().getBytes(), doc.getFormat());

        assertEquals(task, taskService.attachDocumentToTask(task, doc.getNome(), doc.getNome().getBytes(), doc.getFormat()));
    }

    private static Stream<Arguments> provideAttachDocumentToTask() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        Documento doc1 = new Documento("src/main/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);
        Documento doc4 = new Documento("src/main/resources/documents/4", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(4);
        Documento doc5 = new Documento("src/main/resources/documents/5", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(5);
        Documento doc6 = new Documento("src/main/resources/documents/6", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(6);

        return Stream.of(
                Arguments.of(task1, doc1),
                Arguments.of(task2, doc2),
                Arguments.of(task3, doc3),
                Arguments.of(task4, doc4),
                Arguments.of(task5, doc5),
                Arguments.of(task6, doc6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCurrentPersonaCanCreateTask")
    void currentPersonaCanCreateTask(final Task task, Persona persona, Supergruppo supergruppo) {
        when(gruppoService.getCurrentPersona()).thenReturn(persona);
        when(gruppoService.isResponsabile(persona.getId(), supergruppo.getId())).thenReturn(true);

        assertEquals(true, taskService.currentPersonaCanCreateTask(supergruppo.getId()));
    }

    private static Stream<Arguments> provideCurrentPersonaCanCreateTask() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);
        Supergruppo gruppo2 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        gruppo2.setId(2);
        Supergruppo gruppo3 = new Supergruppo("Accompaganmento al lavoro", "commissione", true);
        gruppo3.setId(3);

        return Stream.of(
                Arguments.of(task1, persona1, gruppo1),
                Arguments.of(task2, persona2, gruppo2),
                Arguments.of(task3, persona3, gruppo3),
                Arguments.of(task4, persona1, gruppo1),
                Arguments.of(task5, persona2, gruppo3),
                Arguments.of(task6, persona3, gruppo3)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetApprovedDocumentiOfSupergruppo")
    void getApprovedDocumentiOfSupergruppo(final Task task, Documento doc, Supergruppo supergruppo) {
        List<Documento> docs = new ArrayList<>();
        docs.add(doc);
        task.setSupergruppo(supergruppo);
        when(documentoService.approvedDocuments(task.getSupergruppo().getId())).thenReturn(docs);

        assertEquals(docs, taskService.getApprovedDocumentiOfSupergruppo(task.getSupergruppo().getId()));
    }

    private static Stream<Arguments> provideGetApprovedDocumentiOfSupergruppo() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);

        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        Documento doc1 = new Documento("src/main/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);
        Documento doc4 = new Documento("src/main/resources/documents/4", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(4);
        Documento doc5 = new Documento("src/main/resources/documents/5", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(5);
        Documento doc6 = new Documento("src/main/resources/documents/6", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(6);

        return Stream.of(
                Arguments.of(task1, doc1, gruppo1),
                Arguments.of(task2, doc2, gruppo1),
                Arguments.of(task3, doc3, gruppo1),
                Arguments.of(task4, doc4, gruppo1),
                Arguments.of(task5, doc5, gruppo1),
                Arguments.of(task6, doc6, gruppo1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetPossibleTaskAssegnees")
    void getPossibleTaskAssegnees(Persona persona, Supergruppo supergruppo) {
        List<Persona> persone = new ArrayList<>();
        persone.add(persona);
        when(gruppoService.findAllMembriInSupergruppo(supergruppo.getId())).thenReturn(persone);

        assertEquals(persone, taskService.getPossibleTaskAssegnees(supergruppo.getId()));
    }

    private static Stream<Arguments> provideGetPossibleTaskAssegnees() {

        Persona persona1 = new Persona("Admin", "Admin", "Administrator");
        Persona persona2 = new Persona("giovanni", "magi", "Administrator");
        Persona persona3 = new Persona("Vittorio", "Scarano", "user");
        Persona persona4 = new Persona("dadasda", "Af aaddmin", "Administrator");
        Persona persona5 = new Persona("gioafafafasvanni", "magadadai", "Administrator");
        Persona persona6 = new Persona("Vitto13rio", "Sca3213123rano", "user");

        Supergruppo gruppo1 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        gruppo1.setId(1);


        return Stream.of(
                Arguments.of(persona1, gruppo1),
                Arguments.of(persona2, gruppo1),
                Arguments.of(persona3, gruppo1),
                Arguments.of(persona4, gruppo1),
                Arguments.of(persona5, gruppo1),
                Arguments.of(persona6, gruppo1)
        );
    }

    /*@ParameterizedTest
    @MethodSource("provideForwardApprovedTaskToPqa")
    void forwardApprovedTaskToPqa(final Task task, Documento doc) {
        when(taskDAO.findById(task.getId()).get().getDocumento()).thenReturn(doc);
        taskService.forwardApprovedTaskToPqa(task.getId());
        assertEquals(task.getDocumento(), doc);
    }

    private static Stream<Arguments> provideForwardApprovedTaskToPqa() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        LocalDate date1 = LocalDate.of(2020, 4, 20);
        LocalDate date2 = LocalDate.of(2019, 12, 30);
        LocalDate date3 = LocalDate.of(2021, 1, 5);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");

        Documento doc1 = new Documento("src/main/resources/documents/1", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(1);
        Documento doc2 = new Documento("src/main/resources/documents/2", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(2);
        Documento doc3 = new Documento("src/main/resources/documents/3", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(3);
        Documento doc4 = new Documento("src/main/resources/documents/4", LocalDate.now(), "test.txt", false, "text/plain");
        doc1.setId(4);
        Documento doc5 = new Documento("src/main/resources/documents/5", LocalDate.now(), "test.txt", false, "text/plain");
        doc2.setId(5);
        Documento doc6 = new Documento("src/main/resources/documents/6", LocalDate.now(), "test.txt", false, "text/plain");
        doc3.setId(6);

        return Stream.of(
                Arguments.of(task1, doc1),
                Arguments.of(task2, doc2),
                Arguments.of(task3, doc3),
                Arguments.of(task4, doc4),
                Arguments.of(task5, doc5),
                Arguments.of(task6, doc6)
        );
    }*/
}
