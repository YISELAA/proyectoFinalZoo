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
public class CategoriaCuidadorDao {

    private EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("profinalPU");

    public void guardar(int idCategoria, List<Long> idsEmpleados) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Habitat cat = em.find(Habitat.class, idCategoria);
            if (cat != null) {
                List<Empleado> listaCuidadores = new ArrayList<>();
                for (Long idEmp : idsEmpleados) {
                    Empleado emp = em.find(Empleado.class, idEmp);
                    if (emp != null) {
                        listaCuidadores.add(emp);
                    }
                }
                cat.setCuidadores(listaCuidadores);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void actualizar(int idCategoria, List<Long> idsEmpleados) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Habitat cat = em.find(Habitat.class, idCategoria);
            if (cat != null) {
                List<Empleado> listaCuidadores = new ArrayList<>();
                for (Long idEmp : idsEmpleados) {
                    Empleado emp = em.find(Empleado.class, idEmp);
                    if (emp != null) {
                        listaCuidadores.add(emp);
                    }
                }
                cat.setCuidadores(listaCuidadores);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void eliminar(int idCategoria) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Habitat cat = em.find(Habitat.class, idCategoria);
            if (cat != null && cat.getCuidadores() != null) {
                cat.getCuidadores().clear();
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Habitat> listar() {
        EntityManager em = emf.createEntityManager();
        List<Habitat> lista = null;
        try {
            TypedQuery<Habitat> query = em.createQuery("SELECT DISTINCT c FROM Categoria c LEFT JOIN FETCH c.cuidadores", 
                Habitat.class
            );
            lista = query.getResultList();
        } finally {
            em.close();
        }
        return lista;
    }

    public Habitat buscarPorId(int idCategoria) {
        EntityManager em = emf.createEntityManager();
        Habitat cat = null;
        try {
            cat = em.find(Habitat.class, idCategoria);
            if (cat != null) {
                cat.getCuidadores().size(); // Forzar la carga de la colección Proxy
            }
        } finally {
            em.close();
        }
        return cat;
    }
}