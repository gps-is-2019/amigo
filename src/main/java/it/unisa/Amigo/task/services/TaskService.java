package it.unisa.Amigo.task.services;

import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;

import java.time.LocalDate;
import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Task".
 */
public interface TaskService {
    Persona getAssegnatarioTask(Integer idTask);

    Task definizioneTaskSupergruppo(String descrizione, LocalDate data, String nome, String stato,
                                    Supergruppo supergruppo, Persona persona);

    List<Task> visualizzaTaskUser(int idPersona);

    List<Task> visualizzaTaskSuperGruppo(int idSupergruppo);

    Task getTaskById(Integer id);

    void accettazioneTask(Integer idTask);

    void rifiutoTask(Integer idTask);

    void completaTask(Integer idTask);

    void updateTask(Task taskToUpdate);
}
