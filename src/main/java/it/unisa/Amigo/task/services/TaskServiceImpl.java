package it.unisa.Amigo.task.services;

import it.unisa.Amigo.autenticazione.domanin.User;
import it.unisa.Amigo.consegna.domain.Documento;
import it.unisa.Amigo.gruppo.domain.Supergruppo;
import it.unisa.Amigo.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService
{

    @Override
    public User getAssegnatarioTask(int id) {
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

    @Override
    public List<Task> visualizzaTaskUser(User user) {
        return null;
    }

    @Override
    public List<Task> visualizzaTaskSuperGruppo(Supergruppo supergruppo) {
        return null;
    }

    @Override
    public List<Task> searchTaskById(int id) {
        return null;
    }
}