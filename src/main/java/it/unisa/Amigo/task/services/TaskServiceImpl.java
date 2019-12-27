package it.unisa.Amigo.task.services;

import it.unisa.Amigo.autenticazione.domanin.User;
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
    @Override
    public Persona getAssegnatarioTask(int id) {

        return null;
    }

    @Override
    public Documento getDocumentoTask(int id) {
        return null;
    }

    @Override
    public Boolean addDocumentoTask(Documento documento, int idTask) {
        return null;
    }

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
    @Override
    public List<Task> visualizzaTaskSuperGruppo(int idSupergruppo) {

        List<Task> ris = taskDAO.findAllBySupergruppo_Id(idSupergruppo);
        return ris;
    }


    @Override
    public List<Task> searchTaskById(int id) {
        return null;
    }

    @Override
    public Task getTaskById(int id) {
        Task ris = taskDAO.findById(id);
        return ris;
    }


    @Override
    public void accettazioneTask(int idTask) {
        taskDAO.updateStato(idTask, "approvato");
    }

    @Override
    public void rifiutoTask(int idTask) {
        taskDAO.updateStato(idTask, "respinto");

    }
}