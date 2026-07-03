package com.ues.edu.entidades;

import com.ues.edu.modelo.EncriptarContrasenia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestBase {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("profinalPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // ===========================
            // ROLES DEL SISTEMA
            // ===========================

            Rol admin = new Rol();
            admin.setNombreRol("ADMINISTRADOR");
            em.persist(admin);

            Rol veterinario = new Rol();
            veterinario.setNombreRol("VETERINARIO");
            em.persist(veterinario);

            Rol cuidador = new Rol();
            cuidador.setNombreRol("CUIDADOR");
            em.persist(cuidador);

            // ===========================
            // EMPLEADO ADMINISTRADOR
            // ===========================

            Empleado empleado = new Empleado();
            empleado.setNombre("Karla");
            empleado.setApellido("Ruiz");
            empleado.setDui("00000000-0");

            em.persist(empleado);

            // ===========================
            // USUARIO ADMINISTRADOR
            // ===========================

            EncriptarContrasenia enc = new EncriptarContrasenia();

            Usuario usuario = new Usuario();
            usuario.setNombreUsuario("karla123");
            usuario.setContrasena(enc.contraseniaencriptar("karla123"));
            usuario.setEmpleado(empleado);
            usuario.setRol(admin);

            em.persist(usuario);

            em.getTransaction().commit();

            System.out.println("==================================");
            System.out.println("BASE INICIAL CREADA");
            System.out.println("==================================");
           
        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
            emf.close();
        }
    }
}