/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Habitat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author coc44
 */
public class HabitatDao {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("profinalPU");

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

            // 1. Buscar el hábitat por su ID
            Habitat h = em.find(Habitat.class, id);
            if (h != null) {
                
                // 2. Limpiar la relación ManyToMany en la tabla intermedia (habitat_cuidador)
                // Esto evita errores de clave foránea sin borrar a los empleados
                if (h.getCuidadores() != null) {
                    h.getCuidadores().clear();
                    em.merge(h);
                }

                // 3. Romper la relación ManyToOne con los animales de forma masiva
                // Pone en NULL el campo idhabitat de todos los animales que dependían de este hábitat
                em.createQuery("UPDATE Animal a SET a.habitat = null WHERE a.habitat.id = :id")
                  .setParameter("id", id)
                  .executeUpdate();

                // 4. Ahora que no hay dependencias vivas en la base de datos, eliminamos el hábitat
                em.remove(h);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            // Si algo falla, revertimos los cambios para no dejar datos corruptos
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Lanzamos la excepción para verla en la consola de Tomcat
        } finally {
            em.close();
        }
    }

    // ==========================================================
    // LISTAR TODOS (Corregido: Fijos en su sitio por ID)
    // ==========================================================
    public List<Habitat> listar() {
        EntityManager em = emf.createEntityManager();
        // 🌟 Agregamos ORDER BY h.id ASC
        TypedQuery<Habitat> query =
                em.createQuery("SELECT h FROM Habitat h ORDER BY h.id ASC", Habitat.class);
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

    // ==========================================================
    // PAGINADO (Corregido con orden)
    // ==========================================================
    public List<Habitat> listarPaginado(int pagina, int size) {
        EntityManager em = emf.createEntityManager();
        // 🌟 Es vital mantener el orden aquí para no duplicar datos en las páginas
        TypedQuery<Habitat> query =
                em.createQuery("SELECT h FROM Habitat h ORDER BY h.id ASC", Habitat.class);
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