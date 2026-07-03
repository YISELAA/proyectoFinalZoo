/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author MINED
 */
public class TicketDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Ticket> listarTodos() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Ticket> query = em.createQuery(
                "SELECT t FROM Ticket t ORDER BY t.id ASC", Ticket.class);
        List<Ticket> lista = query.getResultList();
        em.close();
        return lista;
    }

    public void guardar(Ticket ticket) {

        ticket.setEstado("Activo");

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(ticket);
        em.getTransaction().commit();

        em.close();
    }

    public void actualizar(Ticket producto) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(producto);
        em.getTransaction().commit();
        em.close();
    }

    public void deshabilitar(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Ticket ticket = em.find(Ticket.class, id);
        if (ticket != null) {
            ticket.setEstado("Inactivo");
            em.merge(ticket);
        }
        em.getTransaction().commit();
        em.close();
    }

    public void habilitar(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Ticket ticket = em.find(Ticket.class, id);
        if (ticket != null) {
            ticket.setEstado("Activo");
            em.merge(ticket);
        }
        em.getTransaction().commit();
        em.close();
    }

    public Ticket buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        Ticket p = em.find(Ticket.class, id);
        em.close();
        return p;
    }

    public Ticket buscarPorTipo(String tipo) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Ticket> query = em.createQuery(
                "SELECT t FROM Ticket t WHERE t.tipo = :tipo",
                Ticket.class
        );
        query.setParameter("tipo", tipo);
        List<Ticket> result = query.getResultList();
        em.close();
        return result.isEmpty() ? null : result.get(0);
    }

}
