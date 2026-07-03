/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Alimentacion;
import com.ues.edu.entidades.Animal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author coc44
 */
public class AlimentacionDao {

     private EntityManagerFactory emf = JPAUtil.getEMF();

    // ==========================
    // GUARDAR (BLINDADO CONTRA NULOS)
    // ==========================
    public void guardar(Alimentacion alimentacion) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // 🔥 1. BUSCAR EL ANIMAL REAL EN LA BD (Evita duplicados)
            if (alimentacion.getAnimal() != null && alimentacion.getAnimal().getId() != null) {
                Animal animal = em.find(Animal.class, alimentacion.getAnimal().getId());
                alimentacion.setAnimal(animal);
            }

            // 🔥 2. BUSCAR EL CUIDADOR REAL EN LA BD (Evita NullPointerException)
            if (alimentacion.getCuidador() != null && alimentacion.getCuidador().getId() != null) {
                com.ues.edu.entidades.Empleado cuidador = em.find(
                        com.ues.edu.entidades.Empleado.class, 
                        (long) alimentacion.getCuidador().getId()
                );
                alimentacion.setCuidador(cuidador);
            } else {
                alimentacion.setCuidador(null); // Si va vacío, se guarda de forma segura
            }

            em.persist(alimentacion);
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================
    // ACTUALIZAR (BLINDADO CONTRA NULOS)
    // ==========================
    public void actualizar(Alimentacion alimentacion) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Alimentacion existente = em.find(Alimentacion.class, alimentacion.getId());

            if (existente != null) {
                // Sincronizar el animal de forma segura
                if (alimentacion.getAnimal() != null && alimentacion.getAnimal().getId() != null) {
                    Animal animal = em.find(Animal.class, alimentacion.getAnimal().getId());
                    existente.setAnimal(animal);
                }

                existente.setTipoAlimento(alimentacion.getTipoAlimento());
                existente.setHorario(alimentacion.getHorario());
                existente.setCantidad(alimentacion.getCantidad());
                
                // Sincronizar el cuidador protegiendo si viene vacío (Sin cuidador)
                if (alimentacion.getCuidador() != null && alimentacion.getCuidador().getId() != null) {
                    com.ues.edu.entidades.Empleado cuidador = em.find(
                            com.ues.edu.entidades.Empleado.class,
                            (long) alimentacion.getCuidador().getId()
                    );
                    existente.setCuidador(cuidador);
                } else {
                    existente.setCuidador(null); // Evita caídas catastróficas por nulos al editar
                }
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================
    // ELIMINAR
    // ==========================
    public void eliminar(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Alimentacion a = em.find(Alimentacion.class, id);
            if (a != null) {
                em.remove(a);
            }
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================================
    // LISTAR TODOS (Corregido: Unificado de menor a mayor por ID)
    // ==========================================================
    public List<Alimentacion> listar() {
        EntityManager em = emf.createEntityManager();

        // 🌟 Forzamos ORDER BY a.id ASC para que vaya id 1, 2, 3... fijo hacia abajo
        TypedQuery<Alimentacion> query = em.createQuery(
                "SELECT a FROM Alimentacion a ORDER BY a.id ASC",
                Alimentacion.class
        );

        List<Alimentacion> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ==========================
    // BUSCAR POR ID
    // ==========================
    public Alimentacion buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        Alimentacion a = em.find(Alimentacion.class, id);
        em.close();
        return a;
    }
}