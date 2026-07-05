package com.ues.edu.daos;

import com.ues.edu.entidades.DetalleVisita;
import com.ues.edu.entidades.Ticket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class DetalleVisitaDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    
    public List<DetalleVisita> listar() {

        EntityManager em = emf.createEntityManager();

        
        TypedQuery<DetalleVisita> query = em.createQuery(
                "SELECT d FROM DetalleVisita d JOIN FETCH d.ticket LEFT JOIN FETCH d.empleado ORDER BY d.id ASC",
                DetalleVisita.class
        );

        List<DetalleVisita> lista = query.getResultList();
        em.close();

        return lista;
    }

   
    public void guardar(DetalleVisita detalleVisita) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Busca el ticket real en BD por su id
        if (detalleVisita.getTicket() != null && detalleVisita.getTicket().getId() != null) {
            Ticket ticketManaged = em.find(Ticket.class, detalleVisita.getTicket().getId());
            detalleVisita.setTicket(ticketManaged);
        }

        em.persist(detalleVisita);
        em.getTransaction().commit();
        em.close();
    }

   
    public void actualizar(DetalleVisita detalleVisita) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        try {
            // Buscar el registro original para conservar la fecha
            DetalleVisita original = em.find(DetalleVisita.class, detalleVisita.getId());
            if (original != null) {
                detalleVisita.setFechaVisita(original.getFechaVisita());
            }

            if (detalleVisita.getTicket() != null && detalleVisita.getTicket().getId() != null) {
                Ticket ticketManaged = em.find(Ticket.class, detalleVisita.getTicket().getId());
                detalleVisita.setTicket(ticketManaged);
            }

            em.merge(detalleVisita);
            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }
  
    public DetalleVisita buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<DetalleVisita> query = em.createQuery(
                "SELECT d FROM DetalleVisita d JOIN FETCH d.ticket WHERE d.id = :id",
                DetalleVisita.class
        );
        query.setParameter("id", id);
        List<DetalleVisita> result = query.getResultList();
        em.close();
        return result.isEmpty() ? null : result.get(0);
    }

}
