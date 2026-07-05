/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Habitat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author coc44
 */
public class HabitatDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public void guardar(Habitat habitat) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(habitat);
        em.getTransaction().commit();
        em.close();
    }

    public void actualizar(Habitat habitat) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Habitat existente = em.find(Habitat.class, habitat.getId());
        if (existente != null) {
            existente.setTipoTerreno(habitat.getTipoTerreno());
            existente.setCapacidad(habitat.getCapacidad());
        }

        em.getTransaction().commit();
        em.close();
    }

    public void eliminar(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

        
            Habitat h = em.find(Habitat.class, id);
            if (h != null) {

                if (h.getCuidadores() != null) {
                    h.getCuidadores().clear();
                    em.merge(h);
                }

                em.createQuery("UPDATE Animal a SET a.habitat = null WHERE a.habitat.id = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                em.remove(h);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
           
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; 
        } finally {
            em.close();
        }
    }

  
    public List<Habitat> listar() {
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<Habitat> query
                = em.createQuery("SELECT h FROM Habitat h ORDER BY h.id ASC", Habitat.class);
        List<Habitat> lista = query.getResultList();
        em.close();
        return lista;
    }

    public Habitat buscarPorId(long id) {
        EntityManager em = emf.createEntityManager();
        Habitat h = em.find(Habitat.class, id);
        em.close();
        return h;
    }

   
    public List<Habitat> listarPaginado(int pagina, int size) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Habitat> query
                = em.createQuery("SELECT h FROM Habitat h ORDER BY h.id ASC", Habitat.class);
        query.setFirstResult((pagina - 1) * size);
        query.setMaxResults(size);
        List<Habitat> lista = query.getResultList();
        em.close();
        return lista;
    }

    public boolean existeTipoTerreno(String tipo) {
        EntityManager em = emf.createEntityManager();

        Long count = em.createQuery(
                "SELECT COUNT(h) FROM Habitat h WHERE LOWER(h.tipoTerreno) = LOWER(:tipo)",
                Long.class
        )
                .setParameter("tipo", tipo)
                .getSingleResult();

        em.close();
        return count > 0;
    }
}
