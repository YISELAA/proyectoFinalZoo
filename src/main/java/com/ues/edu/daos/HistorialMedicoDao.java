/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Animal;
import com.ues.edu.entidades.Empleado;
import com.ues.edu.entidades.HistorialMedico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 *
 * @author coc44
 */
public class HistorialMedicoDao {

      private EntityManagerFactory emf = JPAUtil.getEMF();


    // ==========================
    // GUARDAR
    // ==========================
    public void guardar(HistorialMedico historial) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Animal animalReal = em.find(Animal.class, historial.getAnimal().getId());
            Empleado veterinarioReal = em.find(Empleado.class, historial.getVeterinario().getId());
            
            historial.setAnimal(animalReal);
            historial.setVeterinario(veterinarioReal);
            em.merge(historial);
            
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

    // ==========================
    // ACTUALIZAR
    // ==========================
    public void actualizar(HistorialMedico historial) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Animal animalReal = em.find(Animal.class, historial.getAnimal().getId());
            Empleado veterinarioReal = em.find(Empleado.class, historial.getVeterinario().getId());
            
            historial.setAnimal(animalReal);
            historial.setVeterinario(veterinarioReal);
            
            em.merge(historial);
            
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

    // ==========================
    // ELIMINAR (DESHABILITADO)
    // ==========================
    public void eliminar(int id) {
        throw new UnsupportedOperationException("La eliminación de registros médicos está deshabilitada.");
    }

    // ==========================================================
    // LISTAR TODOS (Corregido: Igual a las demás tablas por ID)
    // ==========================================================
    public List<HistorialMedico> listar() {
        EntityManager em = emf.createEntityManager();
        // 🌟 Cambiado a h.id ASC para mantener la simetría con el resto del sistema
        TypedQuery<HistorialMedico> query =
                em.createQuery("SELECT h FROM HistorialMedico h ORDER BY h.id ASC", HistorialMedico.class);
        List<HistorialMedico> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ==========================
    // BUSCAR POR ID
    // ==========================
    public HistorialMedico buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        HistorialMedico h = em.find(HistorialMedico.class, id);
        em.close();
        return h;
    }

    // ==========================================================
    // BUSCAR POR DIAGNÓSTICO (Corregido con orden)
    // ==========================================================
    public List<HistorialMedico> buscarPorDiagnostico(String diagnostico) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<HistorialMedico> query =
                em.createQuery(
                        "SELECT h FROM HistorialMedico h WHERE LOWER(h.diagnostico) LIKE LOWER(:diagnostico) ORDER BY h.id ASC",
                        HistorialMedico.class
                );
        query.setParameter("diagnostico", "%" + diagnostico + "%");
        List<HistorialMedico> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ==========================================================
    // FILTRAR POR FECHA (Corregido con orden)
    // ==========================================================
    public List<HistorialMedico> filtrarPorFecha(Date fecha) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<HistorialMedico> query =
                em.createQuery(
                        "SELECT h FROM HistorialMedico h WHERE h.fecha = :fecha ORDER BY h.id ASC",
                        HistorialMedico.class
                );
        query.setParameter("fecha", fecha);
        List<HistorialMedico> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ==========================================================
    // FILTRAR POR VETERINARIO (Corregido con orden)
    // ==========================================================
    public List<HistorialMedico> filtrarPorVeterinario(int idVeterinario) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<HistorialMedico> query =
                em.createQuery(
                        "SELECT h FROM HistorialMedico h WHERE h.veterinario.id = :idVeterinario ORDER BY h.id ASC",
                        HistorialMedico.class
                );
        query.setParameter("idVeterinario", idVeterinario);
        List<HistorialMedico> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ==========================================================
    // FILTRAR POR ANIMAL (Corregido con orden)
    // ==========================================================
    public List<HistorialMedico> filtrarPorAnimal(int idAnimal) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<HistorialMedico> query =
                em.createQuery(
                        "SELECT h FROM HistorialMedico h WHERE h.animal.id = :idAnimal ORDER BY h.id ASC",
                        HistorialMedico.class
                );
        query.setParameter("idAnimal", idAnimal);
        List<HistorialMedico> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ==========================================================
    // PAGINACIÓN (Corregido con orden)
    // ==========================================================
    public List<HistorialMedico> listarPaginado(int pagina, int size) {
        EntityManager em = emf.createEntityManager();
        // 🌟 Se alinea estrictamente al ordenamiento ascendente por ID
        TypedQuery<HistorialMedico> query =
                em.createQuery("SELECT h FROM HistorialMedico h ORDER BY h.id ASC", HistorialMedico.class);
        query.setFirstResult((pagina - 1) * size);
        query.setMaxResults(size);
        List<HistorialMedico> lista = query.getResultList();
        em.close();
        return lista;
    }
}