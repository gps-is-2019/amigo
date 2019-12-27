package it.unisa.Amigo.task.services;

import it.unisa.Amigo.consegna.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Persona;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.dao.TaskDAO;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService
{

    @Autowired
    private TaskDAO taskDAO;



    //TODO va cambiato
//    @Override
//    public User getAssegnatarioTask(int id) {
//        return null;
//    }

    /***
     * Ritorna l'user @{@link Persona} incaricato del Task @{@link Task}
     * @param id del task assegnato
     * @return persona
     */
    @Override
    public Persona getAssegnatarioTask(int id) {

        return null;
    }

    /***
     * Ritorna il documento @{@link Documento} del Task @{@link Task}
     * @param id del task di cui si vuole l'id
     * @return documento
     */
    @Override
    public Documento getDocumentoTask(int id) {
        return null;
    }

    /***
     * Aggiunge un documento @{@link Documento} al Task @{@link Task}
     * @param documento da aggiungere al task
     * @param idTask id del task al cui aggiungere il documento
     * @return Boolean
     */
    @Override
    public Boolean addDocumentoTask(Documento documento, int idTask) {

        return null;

    }

    /***
     * Definisce un nuovo Task @{@link Task}
     * @param descrizione del nuovo task
     * @param data di scadenza del nuovo task
     * @param nome del nuovo task
     * @param stato del nuovo task
     * @param supergruppo di appartenenza del task da definire
     * @param email del responsabile del task che si sta definendo
     * @return Boolean indicante il successo o il fallimento dell'azione
     */
    @Override
    public Boolean definizioneTaskSupergruppo(String descrizione, Date data, String nome, String stato, Supergruppo supergruppo, String email) {
        return null;
    }

    //TODO va cambiato

//    @Override
//    public List<Task> visualizzaTaskUser(User user) {
//
//
//        return null;
//    }

    /***
     * Ritorna una lista di task @{@link Task} dell'utente passato
     * @param idPersona id della persona di cui si vuole conosce la lista di task
     * @return lista di task
     */
    @Override
    public List<Task> visualizzaTaskUser(int idPersona) {
        return taskDAO.findAllByPersona_Id(idPersona);
    }

    //TODO da aggiornare su odd da Supergruppo supergruppo ad int idSupergruppo
//    @Override
//    public List<Task> visualizzaTaskSuperGruppo(Supergruppo supergruppo) {
//
//        List<Task> ris = taskDAO.findAllBysupergruppo_id(1);
//        return ris;
//    }

    /***
     * Ritorna la lista di task @{@link Task} del supergruppo @{@link Supergruppo} del supergruppo passato
     * @param idSupergruppo di cui si vogliono visualizzare i task
     * @return lista di task
     */
    @Override
    public List<Task> visualizzaTaskSuperGruppo(int idSupergruppo) {

        List<Task> ris = taskDAO.findAllBySupergruppo_Id(idSupergruppo);
        return ris;
    }

    //TODO
    /***
     * Ritorna la lista di task @{@link Task} cercati tramite un id
     * @param id del task
     * @return lista di task
     */
    @Override
    public List<Task> searchTaskById(int id) { //Da cambiare
        return null;
    }


    /***
     * Ritorna il task @{@link Task} corrispondente dall'id cercato
     * @param id del task
     * @return task
     */
    @Override
    public Task getTaskById(int id) {
        Task ris = taskDAO.findById(id);
        return ris;
    }


}