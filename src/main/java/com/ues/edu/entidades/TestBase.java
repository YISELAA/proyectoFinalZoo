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


            Cargo administrador = new Cargo();
            administrador.setNombreCargo("Administrador");
            administrador.setDescripcion("Administrador del zoológico");
            em.persist(administrador);

            Cargo veterinarioCargo = new Cargo();
            veterinarioCargo.setNombreCargo("Veterinario");
            veterinarioCargo.setDescripcion("Atiende a los animales");
            em.persist(veterinarioCargo);

            Cargo cuidadorCargo = new Cargo();
            cuidadorCargo.setNombreCargo("Cuidador");
            cuidadorCargo.setDescripcion("Cuida y alimenta a los animales");
            em.persist(cuidadorCargo);

  
            Cargo limpiezaHabitats = new Cargo();
            limpiezaHabitats.setNombreCargo("Auxiliar de Limpieza de Hábitats");
            limpiezaHabitats.setDescripcion("Limpieza y desinfección de recintos de animales y manejo de desechos");
            em.persist(limpiezaHabitats);

            Cargo conserjeAreas = new Cargo();
            conserjeAreas.setNombreCargo("Conserje de Áreas Comunes");
            conserjeAreas.setDescripcion("Mantenimiento de la limpieza en pasillos, plazas, baños y oficinas");
            em.persist(conserjeAreas);


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
            empleado.setTelefono("71234567");
            empleado.setCorreo("karla@gmail.com");
            empleado.setSalario(800.00);
            empleado.setCargo(administrador);

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
            System.out.println("BASE INICIAL CREADA CON NUEVOS CARGOS");
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