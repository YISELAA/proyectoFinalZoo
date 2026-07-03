/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Animal;
import jakarta.persistence.EntityManager;
import com.ues.edu.entidades.Habitat;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author coc44
 */
public class AnimalDao {
    
      private EntityManagerFactory emf = JPAUtil.getEMF();

    // ==========================
    // GUARDAR
    // ==========================
    public void guardar(Animal animal) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if (animal.getHabitat() != null && animal.getHabitat().getId() != null) {
            animal.setHabitat(em.find(Habitat.class, animal.getHabitat().getId()));
        }

        em.persist(animal);
        em.getTransaction().commit();
        em.close();
    }

    // ==========================
    // ACTUALIZAR
    // ==========================
    public void actualizar(Animal animal) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if (animal.getHabitat() != null && animal.getHabitat().getId() != null) {
            animal.setHabitat(em.find(Habitat.class, animal.getHabitat().getId()));
        }

        em.merge(animal);
        em.getTransaction().commit();
        em.close();
    }

    // ==========================
    // ELIMINAR
    // ==========================
    public void eliminar(int id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Animal a = em.find(Animal.class, id);
        if (a != null) {
            em.remove(a);
        }
        em.getTransaction().commit();
        em.close();
    }

    // ==========================================================
    // LISTAR TODOS (Corregido: Fijos en su sitio por ID)
    // ==========================================================
    public List<Animal> listar() {
        EntityManager em = emf.createEntityManager();
        
        // 🌟 Cambiamos "JOIN FETCH" por "LEFT JOIN FETCH" para incluir registros con hábitat NULL
        TypedQuery<Animal> query = em.createQuery(
                "SELECT a FROM Animal a LEFT JOIN FETCH a.habitat ORDER BY a.id ASC", 
                Animal.class
        );
        
        List<Animal> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ==========================
    // BUSCAR POR ID (con hábitat)
    // ==========================
    public Animal buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Animal> query =
                em.createQuery("SELECT a FROM Animal a JOIN FETCH a.habitat WHERE a.id = :id", Animal.class);
        query.setParameter("id", id);
        Animal a = null;
        try {
            a = query.getSingleResult();
        } catch (Exception e) {
            // si no existe, devuelve null
        }
        em.close();
        return a;
    }

    // ==========================
    // BUSCAR POR NOMBRE (con hábitat)
    // ==========================
//    public List<Animal> buscarPorNombre(String nombre) {
//        EntityManager em = emf.createEntityManager();
//        TypedQuery<Animal> query =
//                em.createQuery(
//                        "SELECT a FROM Animal a JOIN FETCH a.habitat WHERE LOWER(a.nombre) LIKE LOWER(:nombre) ORDER BY a.id ASC",
//                        Animal.class
//                );
//        query.setParameter("nombre", "%" + nombre + "%");
//        List<Animal> lista = query.getResultList();
//        em.close();
//        return lista;
//    }

    // ==========================
    // FILTRAR POR HÁBITAT
    // ==========================
//    public List<Animal> filtrarPorHabitat(int idHabitat) {
//        EntityManager em = emf.createEntityManager();
//        TypedQuery<Animal> query =
//                em.createQuery(
//                        "SELECT a FROM Animal a JOIN FETCH a.habitat WHERE a.habitat.id = :idHabitat ORDER BY a.id ASC",
//                        Animal.class
//                );
//        query.setParameter("idHabitat", idHabitat);
//        List<Animal> lista = query.getResultList();
//        em.close();
//        return lista;
//    }

    // ==========================================================
    // PAGINACIÓN (Corregido: Indispensable ordenar aquí también)
    // ==========================================================
    public List<Animal> listarPaginado(int pagina, int size) {
        EntityManager em = emf.createEntityManager();
        // 🌟 Agregamos ORDER BY a.id ASC para que la paginación no duplique registros al recargar
        TypedQuery<Animal> query =
                em.createQuery("SELECT a FROM Animal a JOIN FETCH a.habitat ORDER BY a.id ASC", Animal.class);
        query.setFirstResult((pagina - 1) * size);
        query.setMaxResults(size);
        List<Animal> lista = query.getResultList();
        em.close();
        return lista;
    }
}