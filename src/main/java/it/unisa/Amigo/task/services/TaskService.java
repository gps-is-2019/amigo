package it.unisa.Amigo.task.services;


import it.unisa.Amigo.documento.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;


import java.time.LocalDate;
import java.util.List;

/**
 * Questa interfaccia definisce i metodi  per la logica di Business del sottositema "Task"
 */
public interface TaskService {
    Persona getAssegnatarioTask(int id);

    Documento getDocumentoTask(int id);

    Boolean addDocumentoTask(Documento documento, int idTask);
    //List<Documenti> visualizzaDocumentiApprovati(Supergruppo supergruppo);

    Task definizioneTaskSupergruppo(String descrizione, LocalDate data, String nome, String stato,
                                    Supergruppo supergruppo, Persona persona);

    List<Task> visualizzaTaskUser(int idPersona);

    List<Task> visualizzaTaskSuperGruppo(int idSupergruppo);

    Task getTaskById(int id);

    void accettazioneTask(int idTask);

    void rifiutoTask(int idTask);

    void completaTask(int idTask);

    void updateTask(Task taskToUpdate);
}