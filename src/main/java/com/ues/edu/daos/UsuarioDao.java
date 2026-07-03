/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import com.ues.edu.entidades.Empleado;
import com.ues.edu.entidades.Rol;
import com.ues.edu.entidades.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author MINED
 */
public class UsuarioDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Usuario> listar() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u ORDER BY u.id ASC", Usuario.class);
        List<Usuario> lista = query.getResultList();
        em.close();
        return lista;
    }

    // ← SOLO este, borra el de long
    public Usuario buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        Usuario u = em.find(Usuario.class, id);
        em.close();
        return u;
    }

    public void guardar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {

            System.out.println(">>> ROL: " + usuario.getRol());
            System.out.println(">>> ROL ID: " + (usuario.getRol() != null ? usuario.getRol().getId() : "NULL"));
            System.out.println(">>> EMPLEADO: " + usuario.getEmpleado());
            System.out.println(">>> EMPLEADO ID: " + (usuario.getEmpleado() != null ? usuario.getEmpleado().getId() : "NULL"));
            // Resolver Empleado (ya lo tenías)
            if (usuario.getEmpleado() != null && usuario.getEmpleado().getId() != null) {
                Empleado emp = em.find(Empleado.class, usuario.getEmpleado().getId());
                usuario.setEmpleado(emp);
            }

            // ✅ AGREGAR ESTO - Resolver Rol desde la BD
            if (usuario.getRol() != null && usuario.getRol().getId() != null) {
                Rol rol = em.find(Rol.class, usuario.getRol().getId());
                usuario.setRol(rol);
            }

            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar usuario", e);
        } finally {
            em.close();
        }
    }

    public void actualizar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            // Resolver Empleado (ya lo tenías)
            if (usuario.getEmpleado() != null && usuario.getEmpleado().getId() != null) {
                Empleado emp = em.find(Empleado.class, usuario.getEmpleado().getId());
                usuario.setEmpleado(emp);
            }

            // ✅ AGREGAR ESTO también aquí
            if (usuario.getRol() != null && usuario.getRol().getId() != null) {
                Rol rol = em.find(Rol.class, usuario.getRol().getId());
                usuario.setRol(rol);
            }

            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.createNativeQuery("DELETE FROM usuario WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar", e);
        } finally {
            em.close();
        }
    }
    
   public boolean existeNombreUsuario(String nombreUsuario) {
    EntityManager em = emf.createEntityManager();
    try {
        // 🌟 CORREGIDO: Se cambia u.nombre_usuario por u.nombreUsuario
        Long cantidad = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.nombreUsuario = :nombre",
                Long.class)
                .setParameter("nombre", nombreUsuario)
                .getSingleResult();
        return cantidad > 0;
    } finally {
        em.close();
    }
}
}
