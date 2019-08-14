package com.rsoudani.app.commontests.utils;

import org.junit.Ignore;

import javax.persistence.EntityManager;

@Ignore
public class DBCommandTransactionalExecutor {
    EntityManager em;

    public DBCommandTransactionalExecutor(EntityManager em) {
        this.em = em;
    }

    public <T> T executeCommand(DBCommand<T> dbCommand){
        try{
            em.getTransaction().begin();
            T toReturn = dbCommand.execute();
            em.getTransaction().commit();
            em.clear();
            return toReturn;
        }catch (Exception e){
            e.printStackTrace();
            em.getTransaction().rollback();
            throw new IllegalStateException(e);
        }
    }
}
