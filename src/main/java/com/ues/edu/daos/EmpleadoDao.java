package com.ues.edu.daos;

import com.ues.edu.entidades.Empleado;
import com.ues.edu.entidades.Habitat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EmpleadoDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public void guardar(Empleado empleado) {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        em.persist(empleado);

        em.getTransaction().commit();

        em.close();
    }

    public void actualizar(Empleado empleado) {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Empleado existente = em.find(Empleado.class, empleado.getId());

        if (existente != null) {
            existente.setNombre(empleado.getNombre());
            existente.setApellido(empleado.getApellido());
            existente.setDui(empleado.getDui());
        }

        em.getTransaction().commit();

        em.close();
    }

    public void eliminar(int id) {

        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            Empleado e = em.find(Empleado.class, id);

            if (e == null) {
                throw new RuntimeException("Empleado no encontrado");
            }

            if (e.getHabitatsAsignados() != null) {

                for (Habitat h : e.getHabitatsAsignados()) {

                    h.getCuidadores().remove(e);
                }

                e.getHabitatsAsignados().clear();

                em.flush();
            }

            em.remove(e);

            em.getTransaction().commit();

        } catch (Exception ex) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw ex;

        } finally {
            em.close();
        }
    }

    public List<Empleado> listar() {

        EntityManager em = emf.createEntityManager();

        TypedQuery<Empleado> query
                = em.createQuery(
                        "SELECT e FROM Empleado e ORDER BY e.id ASC",
                        Empleado.class
                );

        List<Empleado> lista = query.getResultList();

        em.close();

        return lista;
    }

    public Empleado buscarPorId(int id) {

        EntityManager em = emf.createEntityManager();

        Empleado e = em.find(Empleado.class, id);

        em.close();

        return e;
    }

    public boolean existeDui(String dui) {

        EntityManager em = emf.createEntityManager();

        Long cantidad = em.createQuery(
                "SELECT COUNT(e) FROM Empleado e WHERE e.dui = :dui",
                Long.class)
                .setParameter("dui", dui)
                .getSingleResult();

        em.close();

        return cantidad > 0;
    }

    public List<Empleado> obtenerSoloVeterinarios() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Empleado e WHERE e.rol = 'Veterinario'", Empleado.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Empleado> obtenerSoloCuidadores() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT e FROM Empleado e "
                    + "JOIN Usuario u ON u.empleado = e "
                    + "JOIN u.rol r "
                    + "WHERE UPPER(r.nombreRol) = 'CUIDADOR'",
                    Empleado.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
