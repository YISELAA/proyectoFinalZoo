package com.ues.edu.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import java.util.List;

public class ConsultaAnimalCuidadorDao {

    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Object[]> buscarFiltro(String filtro) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            String sql = "SELECT a.id, a.nombre_animal, a.especie, a.sexo, e.nombre_empleado, e.apellido, e.numero_dui "
                    + "FROM habitat_cuidador hc "
                    + "INNER JOIN habitat h ON hc.idhabitat = h.id "
                    + "INNER JOIN animal a ON a.idhabitat = h.id "
                    + "INNER JOIN empleado e ON hc.idempleado = e.id";

            Query query = em.createNativeQuery(sql);
            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
