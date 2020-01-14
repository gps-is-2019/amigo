package it.unisa.Amigo.task.services;

import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Task".
 */
public interface TaskService {
    Persona getAssegnatarioTask(Integer idTask);

    Task definizioneTaskSupergruppo(String descrizione, LocalDate data, String nome, String stato,
                                    Integer supergruppoId, Integer personaId);

    List<Task> visualizzaTaskUser();

    List<Task> visualizzaTaskSuperGruppo(int idSupergruppo);

    Task getTaskById(Integer id);

    void accettazioneTask(Integer idTask);

    void rifiutoTask(Integer idTask);

    void completaTask(Integer idTask);

    void updateTask(Task taskToUpdate, Integer assegneeId);

    Task attachDocumentToTask(Task t, String fileName, byte[] fileContent, String type);

    boolean currentPersonaCanCreateTask(Integer idSupergruppo);

    List<Documento> getApprovedDocumentiOfSupergruppo(Integer idSupergruppo);

    List<Persona> getPossibleTaskAssegnees(Integer idSupergruppo);

    void forwardApprovedTaskToPqa(Integer taskId);

    boolean currentPersonaCanViewTask(Integer idTask);

    Resource getResourceFromTask(Task t);
}
