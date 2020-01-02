package it.unisa.Amigo.task.services;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskDAO taskDAO;

    @Test
    void getAssegnatarioTask(){
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task = new Task("t1" , tmpDate, "task1" , "in valutazione");
        task.setSupergruppo(supergruppo);
        task.setPersona(persona1);
        when(taskDAO.findById(task.getId())).thenReturn(task);
        Persona actualPersona = taskService.getAssegnatarioTask(task.getId());
        assertEquals(persona1, actualPersona);
    }

    @Test
    void definizioneTaskSupergruppo() {
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        Boolean expected = true;
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Boolean actual = taskService.definizioneTaskSupergruppo("t1" , tmpDate, "task1" , "in valutazione", supergruppo, persona1);
        assertEquals(expected, actual);
    }

    @Test
    void visualizzaTaskUser() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Persona persona1 = new Persona("Persona1","Persona1","Persona");
        Task task1 = new Task("t1" , tmpDate, "task1" , "in valutazione");
        Task task2 = new Task("t2" , tmpDate, "task2" , "in valutazione");
        task1.setPersona(persona1);
        task2.setPersona(persona1);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);
        when(taskDAO.findAllByPersona_Id(persona1.getId())).thenReturn(expectedTasks);
        List<Task> actualTask = taskService.visualizzaTaskUser(persona1.getId());
        assertEquals(expectedTasks, actualTask);
    }

    @Test
    void visualizzaTaskSuperGruppo() {
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Supergruppo supergruppo = new Supergruppo("GAQD Informatica", "gruppo", true);
        Task task1 = new Task("t1" , tmpDate, "task1" , "in valutazione");
        Task task2 = new Task("t2" , tmpDate, "task2" , "in valutazione");
        task1.setSupergruppo(supergruppo);
        task2.setSupergruppo(supergruppo);
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);
        when(taskDAO.findAllBySupergruppo_Id(supergruppo.getId())).thenReturn(expectedTasks);
        List<Task> actualTask = taskService.visualizzaTaskSuperGruppo(supergruppo.getId());
        assertEquals(expectedTasks, actualTask);
    }

    @Test
    void getTaskById(){
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task1 = new Task("t1" , tmpDate, "task1" , "in valutazione");
        when(taskDAO.findById(task1.getId())).thenReturn(task1);
        Task actualTask = taskService.getTaskById(task1.getId());
        assertEquals(task1, actualTask);
    }

    @Test
    void accettazioneTask(){
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task1 = new Task("t1" , tmpDate, "task1" , "in valutazione");
        String expectedStato = "approvato";
        when(taskDAO.findById(task1.getId())).thenReturn(task1);
        taskService.accettazioneTask(task1.getId());
        String actualStato = task1.getStato();
        assertEquals(expectedStato, actualStato);
    }

    @Test
    void rifiutoTask(){
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task1 = new Task("t1" , tmpDate, "task1" , "in valutazione");
        String expectedStato = "respinto";
        when(taskDAO.findById(task1.getId())).thenReturn(task1);
        taskService.rifiutoTask(task1.getId());
        String actualStato = task1.getStato();
        assertEquals(expectedStato, actualStato);
    }

    @Test
    void completaTask(){
        LocalDate tmpDate;
        tmpDate = LocalDate.of(2020, 4, 20);
        Task task1 = new Task("t1" , tmpDate, "task1" , "incompleto");
        String expectedStato = "in valutazione";
        when(taskDAO.findById(task1.getId())).thenReturn(task1);
        taskService.completaTask(task1.getId());
        String actualStato = task1.getStato();
        assertEquals(expectedStato, actualStato);
    }
}
