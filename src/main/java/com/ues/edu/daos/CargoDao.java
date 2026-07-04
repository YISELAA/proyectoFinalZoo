package com.ues.edu.daos;


import com.ues.edu.entidades.Cargo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class CargoDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Cargo> listar() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery("SELECT c FROM Cargo c", Cargo.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}