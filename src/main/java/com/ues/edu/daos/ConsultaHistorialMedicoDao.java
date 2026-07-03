/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author coc44
 */
public class ConsultaHistorialMedicoDao {

    private EntityManagerFactory emf
            = Persistence.createEntityManagerFactory("profinalPU");

    public List<Object[]> buscarFiltro(String filtro) {

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
                    + "WHERE 1=1 ";

            if (filtro != null && !filtro.trim().isEmpty()) {

                sql += " AND (LOWER(a.nombre_animal) LIKE LOWER(:filtro) "
                        + " OR LOWER(a.especie) LIKE LOWER(:filtro) "
                        + " OR LOWER(h.diagnostico) LIKE LOWER(:filtro) "
                        + " OR LOWER(e.nombre_empleado) LIKE LOWER(:filtro)) ";
            }

            sql += " ORDER BY h.fecha DESC ";

            Query query = em.createNativeQuery(sql);

            if (filtro != null && !filtro.trim().isEmpty()) {

                query.setParameter("filtro", "%" + filtro + "%");
            }

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