package it.unisa.Amigo.task.services;


import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;


public interface TaskService {
    //TODO dovrebbe andare Persona non user
    //User getAssegnatarioTask(int id);

    Persona getAssegnatarioTask(int id);


    //TODO ci serve la classe documento
    Documento getDocumentoTask(int id);
    Boolean addDocumentoTask(Documento documento, int idTask);


    Boolean definizioneTaskSupergruppo(String descrizione, Date data, String nome, String stato, Supergruppo supergruppo, String email);
    //TODO anche qui dovremmo passare Persona o nessun parametro
    //List<Task> visualizzaTaskUser(User user);
    List<Task> visualizzaTaskUser(int idPersona);

    //TODO da aggiornare su odd da Supergruppo supergruppo ad int idSupergruppo
    //List<Task> visualizzaTaskSuperGruppo(Supergruppo supergruppo);
    List<Task> visualizzaTaskSuperGruppo(int  idSupergruppo);

    //List<Documenti> visualizzaDocumentiApprovati(Supergruppo supergruppo);
    //TODO da vedere se va cambiato così -> anche in serviceImpl
    List<Task> searchTaskById(int id);
    Task getTaskById(int id);

    void accettazioneTask(int idTask);
    void rifiutoTask(int idTask);

}