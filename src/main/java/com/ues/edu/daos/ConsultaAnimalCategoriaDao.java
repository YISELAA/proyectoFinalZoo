package com.ues.edu.daos;

import com.ues.edu.daos.JPAUtil;
import com.ues.edu.entidades.Animal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class ConsultaAnimalCategoriaDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Animal> buscarFiltro(String filtro) {

        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            return em.createQuery(
                    "SELECT a FROM Animal a JOIN FETCH a.habitat",
                    Animal.class
            ).getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}