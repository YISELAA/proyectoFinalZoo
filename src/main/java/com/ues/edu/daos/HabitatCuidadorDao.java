/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Habitat;
import com.ues.edu.entidades.Empleado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author coc44
 */
public class HabitatCuidadorDao {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("profinalPU");

    // Guardar asignaciones de cuidadores a un hábitat
    public void guardar(int idHabitat, List<Long> idsEmpleados) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Habitat habitat = em.find(Habitat.class, idHabitat);
            if (habitat != null) {
                List<Empleado> listaCuidadores = new ArrayList<>();
                for (Long idEmp : idsEmpleados) {
                    Empleado emp = em.find(Empleado.class, idEmp);
                    if (emp != null) {
                        listaCuidadores.add(emp);
                    }
                }
                // Setea la colección gestionada por @ManyToMany (tabla: habitat_cuidador)
                habitat.setCuidadores(listaCuidadores);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Actualizar/reemplazar los cuidadores de un hábitat
    public void actualizar(int idHabitat, List<Long> idsEmpleados) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Habitat habitat = em.find(Habitat.class, idHabitat);
            if (habitat != null) {
                List<Empleado> listaCuidadores = new ArrayList<>();
                for (Long idEmp : idsEmpleados) {
                    Empleado emp = em.find(Empleado.class, idEmp);
                    if (emp != null) {
                        listaCuidadores.add(emp);
                    }
                }
                // JPA se encarga de hacer el DELETE y luego los INSERT en habitat_cuidador
                habitat.setCuidadores(listaCuidadores);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Vaciar o eliminar todas las asignaciones de un hábitat
    public void eliminar(int idHabitat) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Habitat habitat = em.find(Habitat.class, idHabitat);
            if (habitat != null && habitat.getCuidadores() != null) {
                habitat.getCuidadores().clear();
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================================
    // LISTAR TODOS (Corregido: Igual a las demás tablas por ID)
    // ==========================================================
    public List<Habitat> listar() {
        EntityManager em = emf.createEntityManager();
        List<Habitat> lista = null;
        try {
            // 🌟 Agregamos ORDER BY h.id ASC al final de la consulta JPQL
            TypedQuery<Habitat> query = em.createQuery(
                "SELECT DISTINCT h FROM Habitat h LEFT JOIN FETCH h.cuidadores ORDER BY h.id ASC",
                Habitat.class
            );
            lista = query.getResultList();
        } finally {
            em.close();
        }
        return lista;
    }

    // Buscar un hábitat específico con sus cuidadores cargados
    public Habitat buscarPorId(int idHabitat) {
        EntityManager em = emf.createEntityManager();
        Habitat habitat = null;
        try {
            habitat = em.find(Habitat.class, idHabitat);
            if (habitat != null) {
                habitat.getCuidadores().size(); // Forzar la carga de la colección Proxy
            }
        } finally {
            em.close();
        }
        return habitat;
    }
    
    
}