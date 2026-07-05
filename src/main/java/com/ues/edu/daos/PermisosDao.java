package com.ues.edu.daos;

import com.ues.edu.entidades.OpcionMenu;
import com.ues.edu.entidades.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class PermisosDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<OpcionMenu> listarTodasLasOpciones() {
        EntityManager em = emf.createEntityManager();
        try {
            em.clear(); 
            
            TypedQuery<OpcionMenu> query = em.createQuery(
                "SELECT o FROM OpcionMenu o ORDER BY o.id ASC", 
                OpcionMenu.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.out.println("Error en PermisosDao.listarTodasLasOpciones: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

   
    public List<Integer> obtenerIdsOpcionesPorRol(int idRol) {
        EntityManager em = emf.createEntityManager();
        List<Integer> ids = new ArrayList<>();
        try {
           
            em.clear();
            
            Rol rol = em.find(Rol.class, idRol);
            if (rol != null && rol.getOpcionesMenu() != null) {
              
                rol.getOpcionesMenu().size(); 
                
                for (OpcionMenu op : rol.getOpcionesMenu()) {
                    ids.add(op.getId());
                }
            }
            return ids;
        } catch (Exception e) {
            System.out.println("Error en PermisosDao.obtenerIdsOpcionesPorRol: " + e.getMessage());
            return ids;
        } finally {
            em.close();
        }
    }

    
    public boolean actualizarPermisos(int idRol, List<Integer> idsOpciones) {
        EntityManager em = emf.createEntityManager();
        boolean exito = false;
        try {
            em.getTransaction().begin();

            Rol rol = em.find(Rol.class, idRol);
            if (rol != null) {
                List<OpcionMenu> nuevasOpciones = new ArrayList<>();

                if (idsOpciones != null && !idsOpciones.isEmpty()) {
                    for (Integer idOp : idsOpciones) {
                        OpcionMenu op = em.find(OpcionMenu.class, idOp);
                        if (op != null) {
                            nuevasOpciones.add(op);
                        }
                    }
                }

                
                rol.setOpcionesMenu(nuevasOpciones);
                em.merge(rol);
                
                em.getTransaction().commit();
                exito = true;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Error en PermisosDao.actualizarPermisos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return exito;
    }
}