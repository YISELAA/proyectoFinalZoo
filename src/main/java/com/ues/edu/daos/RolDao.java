package com.ues.edu.daos;

import com.ues.edu.entidades.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class RolDao {
    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Rol> listar() {
        EntityManager em = emf.createEntityManager();
        List<Rol> lista = em.createQuery("SELECT r FROM Rol r", Rol.class).getResultList();
        em.close();
        return lista;
    }
}