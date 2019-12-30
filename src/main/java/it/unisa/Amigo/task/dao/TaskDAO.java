package it.unisa.Amigo.task.dao;


import it.unisa.Amigo.task.domain.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Questa interfaccia si occupa di fornire un accesso astratto  all'oggetto di dominio "Task"
 */
@Repository
public interface TaskDAO extends CrudRepository<Task, Integer> {
    //    List<Task> findAllByDataDataScadenza(Date dataScadenza);
//    List<Task> findAllByNome(String nome);

    //List<Task> findAllBySupergruppo_id(int idSupergruppo);
    List<Task> findAllBySupergruppo_Id(int idSupergruppo);
    List<Task> findAllByPersona_Id(int id);
    Task findById(int idTask);

}