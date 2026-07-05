package com.ues.edu.daos;

import com.ues.edu.entidades.Animal;
import jakarta.persistence.EntityManager;
import com.ues.edu.entidades.Habitat;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class AnimalDao {
    
      private EntityManagerFactory emf = JPAUtil.getEMF();


    public void guardar(Animal animal) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if (animal.getHabitat() != null && animal.getHabitat().getId() != null) {
            animal.setHabitat(em.find(Habitat.class, animal.getHabitat().getId()));
        }

        em.persist(animal);
        em.getTransaction().commit();
        em.close();
    }

    public void actualizar(Animal animal) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if (animal.getHabitat() != null && animal.getHabitat().getId() != null) {
            animal.setHabitat(em.find(Habitat.class, animal.getHabitat().getId()));
        }

        em.merge(animal);
        em.getTransaction().commit();
        em.close();
    }

    public void eliminar(int id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Animal a = em.find(Animal.class, id);
        if (a != null) {
            em.remove(a);
        }
        em.getTransaction().commit();
        em.close();
    }

    public List<Animal> listar() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Animal> query =
                em.createQuery("SELECT a FROM Animal a JOIN FETCH a.habitat ORDER BY a.id ASC", Animal.class);
        List<Animal> lista = query.getResultList();
        em.close();
        return lista;
    }

    public Animal buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Animal> query =
                em.createQuery("SELECT a FROM Animal a JOIN FETCH a.habitat WHERE a.id = :id", Animal.class);
        query.setParameter("id", id);
        Animal a = null;
        try {
            a = query.getSingleResult();
        } catch (Exception e) {
        }
        em.close();
        return a;
    }

    public List<Animal> buscarPorNombre(String nombre) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Animal> query =
                em.createQuery(
                        "SELECT a FROM Animal a JOIN FETCH a.habitat WHERE LOWER(a.nombre) LIKE LOWER(:nombre) ORDER BY a.id ASC",
                        Animal.class
                );
        query.setParameter("nombre", "%" + nombre + "%");
        List<Animal> lista = query.getResultList();
        em.close();
        return lista;
    }

    public List<Animal> filtrarPorHabitat(int idHabitat) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Animal> query =
                em.createQuery(
                        "SELECT a FROM Animal a JOIN FETCH a.habitat WHERE a.habitat.id = :idHabitat ORDER BY a.id ASC",
                        Animal.class
                );
        query.setParameter("idHabitat", idHabitat);
        List<Animal> lista = query.getResultList();
        em.close();
        return lista;
    }

    public List<Animal> listarPaginado(int pagina, int size) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Animal> query =
                em.createQuery("SELECT a FROM Animal a JOIN FETCH a.habitat ORDER BY a.id ASC", Animal.class);
        query.setFirstResult((pagina - 1) * size);
        query.setMaxResults(size);
        List<Animal> lista = query.getResultList();
        em.close();
        return lista;
    }
}