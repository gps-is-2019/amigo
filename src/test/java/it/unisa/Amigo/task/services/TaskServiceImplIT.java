package it.unisa.Amigo.task.services;

import it.unisa.Amigo.gruppo.dao.PersonaDAO;
import it.unisa.Amigo.gruppo.dao.SupergruppoDAO;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TaskServiceImplIT {

    @Autowired
    private TaskServiceImpl taskService;

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private PersonaDAO personaDAO;

    @Autowired
    private SupergruppoDAO supergruppoDAO;

    @Test
    void getAssegnatarioTask() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "in valutazione");
        task.setPersona(persona1);
        persona1.addTask(task);
        taskDAO.save(task);
        Persona actualPersona = taskService.getAssegnatarioTask(task.getId());
        assertEquals(persona1, actualPersona);
    }

    @Test
    void definizioneTaskSupergruppo() {
        Persona persona1 = new Persona("Persona1", "Persona1", "Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task expected = new Task("t1", tmpDate, "task1", "in valutazione");
        expected.setSupergruppo(supergruppo);
        expected.setPersona(persona1);
        Task actual = taskService.definizioneTaskSupergruppo("t1", tmpDate, "task1", "in valutazione", supergruppo, persona1);
        expected.setId(actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    void visualizzaTaskUser() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Persona persona = new Persona("Persona1", "Persona1", "Persona");
        Task task1 = new Task("t1", tmpDate, "task1", "in valutazione");
        Task task2 = new Task("t2", tmpDate, "task2", "in valutazione");
        task1.setPersona(persona);
        task2.setPersona(persona);
        persona.addTask(task1);
        persona.addTask(task2);
        taskDAO.save(task1);
        taskDAO.save(task2);
        personaDAO.save(persona);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);
        List<Task> actualTask = taskService.visualizzaTaskUser(persona.getId());
        assertEquals(expectedTasks, actualTask);
    }

    @Test
    void visualizzaTaskSuperGruppo() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        Task task1 = new Task("t1", tmpDate, "task1", "in valutazione");
        Task task2 = new Task("t2", tmpDate, "task2", "in valutazione");
        task1.setSupergruppo(supergruppo);
        task2.setSupergruppo(supergruppo);
        supergruppo.addTask(task1);
        supergruppo.addTask(task2);
        taskDAO.save(task1);
        taskDAO.save(task2);
        supergruppoDAO.save(supergruppo);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);
        List<Task> actualTask = taskService.visualizzaTaskSuperGruppo(supergruppo.getId());
        assertEquals(expectedTasks, actualTask);
    }

    @Test
    void getTaskById() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "in valutazione");
        taskDAO.save(task);
        Task actualTask = taskService.getTaskById(task.getId());
        assertEquals(task, actualTask);
    }

    @Test
    void accettazioneTask() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "in valutazione");
        taskDAO.save(task);
        String expectedstato = "approvato";
        taskService.accettazioneTask(task.getId());
        String actualStato = taskDAO.findById(task.getId()).getStato();
        assertEquals(expectedstato, actualStato);
    }

    @Test
    void rifiutoTask() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "in valutazione");
        taskDAO.save(task);
        String expectedstato = "respinto";
        taskService.rifiutoTask(task.getId());
        String actualStato = taskDAO.findById(task.getId()).getStato();
        assertEquals(expectedstato, actualStato);
    }

    @Test
    void completaTask() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "incompleto");
        taskDAO.save(task);
        String expectedstato = "in valutazione";
        taskService.completaTask(task.getId());
        String actualStato = taskDAO.findById(task.getId()).getStato();
        assertEquals(expectedstato, actualStato);
    }

    @Test
    void updateTask() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1", tmpDate, "task1", "incompleto");
        taskDAO.save(task);
        task.setDescrizione("t2");
        task.setNome("task2");
        task.setStato("in valutazione");
        taskService.updateTask(task);
        Task expectedTask = new Task("t2", tmpDate, "task2", "in valutazione");
        expectedTask.setId(task.getId());
        Task actualTask = taskDAO.findById(task.getId());
        assertEquals(expectedTask, actualTask);
    }

}
