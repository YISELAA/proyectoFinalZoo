/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author coc44
 */
public class ConsultaHistorialMedicoDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Object[]> buscarFiltro() {

        EntityManager em = null;

        try {

            em = emf.createEntityManager();

            String sql
                    = "SELECT "
                    + "a.nombre_animal AS col0, "
                    + "a.especie AS col1, "
                    + "h.diagnostico AS col2, "
                    + "h.tratamiento AS col3, "
                    + "h.fecha AS col4, "
                    + "e.nombre_empleado AS col5, "
                    + "e.apellido AS col6 "
                    + "FROM historial_medico h "
                    + "INNER JOIN animal a ON h.idanimal = a.id "
                    + "INNER JOIN empleado e ON h.idveterinario = e.id "
                    + "ORDER BY h.fecha DESC ";

            Query query = em.createNativeQuery(sql);

            return query.getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}