package it.unisa.Amigo.task.dao;


import it.unisa.Amigo.task.domain.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskDAO extends CrudRepository<Task, Integer> {
//    Task findById (int idTask);
//    List<Task> findAllByDataDataScadenza(Date dataScadenza);
//    List<Task> findAllByNome(String nome);
//
//    List<Task> findAllBysupergruppo_id(int idSupergruppo);



}