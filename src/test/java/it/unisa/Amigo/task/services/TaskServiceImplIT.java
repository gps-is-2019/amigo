package it.unisa.Amigo.task.services;

import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TaskServiceImplIT {

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @ParameterizedTest
    @MethodSource("provideGetAssegnatarioTask")
    void getAssegnatarioTask(Persona persona1, Task task) {
        taskDAO.save(task);
        Persona actualPersona = taskService.getAssegnatarioTask(task.getId());
        assertEquals(persona1, actualPersona);
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
        persona1.addTask(task);
        persona1.addTask(task2);
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
    void definizioneTaskSupergruppo(String descrizione, LocalDate dataScadenza, String nome, String stato,
                                    Supergruppo s, Persona persona, Task expectedTask) {
        Task actual = taskService.definizioneTaskSupergruppo(descrizione, dataScadenza, nome, stato, s, persona);
        expectedTask.setId(actual.getId());
        assertEquals(expectedTask, actual);
    }

    private static Stream<Arguments> provideDefinizioneTaskSupergruppo() {
        LocalDate tmpDate;
        LocalDate tmpDate2;
        LocalDate tmpDate3;
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

        Task task1 = new Task("descrizione 1", tmpDate = LocalDate.of(2020, 4, 20), "Task 1", "incompleto");
        task1.setPersona(persona1);
        task1.setSupergruppo(supergruppo1);
        Task task2 = new Task("descrizione 2", tmpDate2 = LocalDate.of(2069, 12, 31), "Task 2", "in valutazione");
        task2.setPersona(persona2);
        task2.setSupergruppo(supergruppo2);
        Task task3 = new Task("descrizione 3", tmpDate3 = LocalDate.of(2120, 1, 1), "Task 3", "respinto");
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
    void visualizzaTaskUser(Persona persona, List<Task> expectedTasks) {
        for (Task expectedTask : expectedTasks) {
            taskDAO.save(expectedTask);
            expectedTask.setPersona(persona);
        }
        personaDAO.save(persona);

        assertEquals(expectedTasks, taskService.visualizzaTaskUser(persona.getId()));
    }

    private static Stream<Arguments> provideVisualizzaTaskUser() {
        LocalDate tmpDate = LocalDate.of(2020, 4, 20);
        LocalDate tmpDate2 = LocalDate.of(2029, 12, 31);
        LocalDate tmpDate3 = LocalDate.of(2120, 1, 1);
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Persona persona2 = new Persona("Persona2", "2", "PQA");
        Persona persona3 = new Persona("Persona3", "3", "DePrisco");
        Persona persona4 = new Persona("Admin", "Admin", "Administrator");
        Persona persona5 = new Persona("giovanni", "magi", "Administrator");
        Persona persona6 = new Persona("Vittorio", "Scarano", "user");
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        task1.setPersona(persona1);
        task2.setPersona(persona2);
        task3.setPersona(persona3);
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", tmpDate3, "task1", "completo");
        task4.setPersona(persona4);
        Task task5 = new Task("t1", tmpDate, "task2", "incompleto");
        task5.setPersona(persona5);
        Task task6 = new Task("t1", tmpDate2, "chiamare azienda", "incompleto");
        task6.setPersona(persona6);
        ArrayList<Task> persona1Tasks = new ArrayList<>();
        persona1Tasks.add(task1);
        ArrayList<Task> persona2Tasks = new ArrayList<>();
        persona2Tasks.add(task2);
        ArrayList<Task> persona3Tasks = new ArrayList<>();
        persona3Tasks.add(task3);
        ArrayList<Task> persona4Tasks = new ArrayList<>();
        persona4Tasks.add(task4);
        ArrayList<Task> persona5Tasks = new ArrayList<>();
        persona5Tasks.add(task5);
        ArrayList<Task> persona6Tasks = new ArrayList<>();
        persona6Tasks.add(task6);

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
    void visualizzaTaskSuperGruppo(Supergruppo supergruppo, List<Task> expectedTasks) {
        for (Task expectedTask : expectedTasks) taskDAO.save(expectedTask);

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
        Supergruppo supergruppo2 = new Supergruppo("Robe", "commisione", false);
        Supergruppo supergruppo3 = new Supergruppo("Boh", "gruppo", true);
        Supergruppo supergruppo4 = new Supergruppo("GAQD- Informatica", "gruppo", true);
        Supergruppo supergruppo5 = new Supergruppo("accompagnamento al lavoro", "commissione", true);
        Supergruppo supergruppo6 = new Supergruppo("GAQR- Informatica", "gruppo", true);
        Task task1 = new Task("descrizione 1", tmpDate, "Task 1", "incompleto");
        Task task2 = new Task("descrizione 2", tmpDate2, "Task 2", "in valutazione");
        Task task3 = new Task("descrizione 3", tmpDate3, "Task 3", "respinto");
        Task task4 = new Task("descrizione lunga vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", date1, "task1", "completo");
        task4.setSupergruppo(supergruppo4);
        Task task5 = new Task("t1", date2, "task2", "incompleto");
        task5.setSupergruppo(supergruppo5);
        Task task6 = new Task("t1", date3, "chiamare azienda", "incompleto");
        task6.setSupergruppo(supergruppo6);
        task1.setSupergruppo(supergruppo1);
        task2.setSupergruppo(supergruppo2);
        task3.setSupergruppo(supergruppo3);
        ArrayList<Task> supergruppo1Tasks = new ArrayList<>();
        supergruppo1Tasks.add(task1);
        ArrayList<Task> supergruppo2Tasks = new ArrayList<>();
        supergruppo2Tasks.add(task2);
        ArrayList<Task> supergruppo3Tasks = new ArrayList<>();
        supergruppo3Tasks.add(task3);
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
    void getTaskById(Task task) {
        taskDAO.save(task);
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
    void accettazioneTask(Task task) {
        taskDAO.save(task);
        taskService.accettazioneTask(task.getId());
        String expectedStato = "approvato";
        assertEquals(expectedStato, taskDAO.findById(task.getId()).get().getStato());
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
    void rifiutoTask(Task task) {
        taskDAO.save(task);
        taskService.rifiutoTask(task.getId());

        String expectedStato = "respinto";
        assertEquals(expectedStato, taskDAO.findById(task.getId()).get().getStato());
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
    void completaTask(Task task) {
        taskDAO.save(task);
        taskService.completaTask(task.getId());

        String expectedStato = "in valutazione";
        assertEquals(expectedStato, taskDAO.findById(task.getId()).get().getStato());
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
}
