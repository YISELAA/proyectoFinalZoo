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
 * @author misa_
 */
public class ConsultaEmpleadoDao {
    
    private EntityManagerFactory emf = JPAUtil.getEMF();

    public List<Object[]> listarEmpleados(String filtro) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            
            // Consulta directa e inalterable
            String sql = "SELECT "
                       + "e.id, "               // a[0] -> id_empleado
                       + "e.nombre_empleado, "  // a[1] -> nombre_empleado
                       + "e.apellido, "         // a[2] -> apellido
                       + "e.numero_dui, "       // a[3] -> numero_dui
                       + "u.nombre_usuario, "   // a[4] -> nombre_usuario
                       + "r.nombre_rol "        // a[5] -> nombre_rol
                       + "FROM empleado e "
                       + "LEFT JOIN usuario u ON u.id_empleado = e.id "
                       + "LEFT JOIN rol r ON r.id = u.idrol "
                       + "ORDER BY e.apellido ASC";

            Query query = em.createNativeQuery(sql);
            return query.getResultList();
            
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
}
