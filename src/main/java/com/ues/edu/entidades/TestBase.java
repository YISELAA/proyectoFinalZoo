package com.ues.edu.entidades;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class TestBase {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("profinalPU");
        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

           

           

            // Crear hábitat
            Habitat habitat = new Habitat();
            habitat.setTipoTerreno("Selva");
            habitat.setCapacidad(20);

            List<Empleado> cuidadores = new ArrayList<>();
          

            habitat.setCuidadores(cuidadores);

            em.persist(habitat);

            em.getTransaction().commit();

            System.out.println("Datos guardados correctamente");

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