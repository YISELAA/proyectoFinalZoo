package com.ues.edu.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import java.util.List;

/**
 * @author coc44
 */
public class ConsultaAlimentacionAnimalDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Object[]> buscarFiltro() {

        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            String sql
                    = "SELECT "
                    + "a.especie AS col0, "
                    + "al.tipo_alimento AS col1, "
                    + "al.cantidad AS col2, "
                    + "al.horario AS col3, "
                    + "CONCAT(COALESCE(c.nombre_empleado, 'Sin Cuidador'), ' ', COALESCE(c.apellido, '')) AS col4 " 
                    + "FROM alimentacion al "
                    + "LEFT JOIN animal a ON al.idanimal = a.id "
                    + "LEFT JOIN empleado c ON al.idcuidador = c.id ";

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