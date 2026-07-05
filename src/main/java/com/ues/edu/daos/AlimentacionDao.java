/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Alimentacion;
import com.ues.edu.entidades.Animal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author coc44
 */
public class AlimentacionDao {

     private EntityManagerFactory emf = JPAUtil.getEMF();

    
    public void guardar(Alimentacion alimentacion) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // BUSCAR EL ANIMAL REAL EN LA BD (Evita duplicados)
            if (alimentacion.getAnimal() != null && alimentacion.getAnimal().getId() != null) {
                Animal animal = em.find(Animal.class, alimentacion.getAnimal().getId());
                alimentacion.setAnimal(animal);
            }

            // BUSCAR EL CUIDADOR REAL EN LA BD (Evita NullPointerException)
            if (alimentacion.getCuidador() != null && alimentacion.getCuidador().getId() != null) {
                com.ues.edu.entidades.Empleado cuidador = em.find(
                        com.ues.edu.entidades.Empleado.class, 
                        (long) alimentacion.getCuidador().getId()
                );
                alimentacion.setCuidador(cuidador);
            } else {
                alimentacion.setCuidador(null); 
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

   
    public void actualizar(Alimentacion alimentacion) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Alimentacion existente = em.find(Alimentacion.class, alimentacion.getId());

            if (existente != null) {
                
                if (alimentacion.getAnimal() != null && alimentacion.getAnimal().getId() != null) {
                    Animal animal = em.find(Animal.class, alimentacion.getAnimal().getId());
                    existente.setAnimal(animal);
                }

                existente.setTipoAlimento(alimentacion.getTipoAlimento());
                existente.setHorario(alimentacion.getHorario());
                existente.setCantidad(alimentacion.getCantidad());
                
                
                if (alimentacion.getCuidador() != null && alimentacion.getCuidador().getId() != null) {
                    com.ues.edu.entidades.Empleado cuidador = em.find(
                            com.ues.edu.entidades.Empleado.class,
                            (long) alimentacion.getCuidador().getId()
                    );
                    existente.setCuidador(cuidador);
                } else {
                    existente.setCuidador(null); 
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

  
    public List<Alimentacion> listar() {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Alimentacion> query = em.createQuery(
                "SELECT a FROM Alimentacion a ORDER BY a.id ASC",
                Alimentacion.class
        );

        List<Alimentacion> lista = query.getResultList();
        em.close();
        return lista;
    }

    public Alimentacion buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        Alimentacion a = em.find(Alimentacion.class, id);
        em.close();
        return a;
    }
}