package com.ues.edu.service;

import com.ues.edu.daos.DetalleVisitaDao;
import com.ues.edu.entidades.DetalleVisita;
import com.ues.edu.entidades.Ticket;
import java.util.List;

public class DetalleVisitaService {

    DetalleVisitaDao dao = new DetalleVisitaDao();

    public List<DetalleVisita> listarDetalleVisita() {
        return dao.listar();
    }

    public DetalleVisita buscarDetalleVisita(int id) {
        return dao.buscarPorId(id);
    }

  
    public void guardarDetalleVisita(DetalleVisita detalle) {
        // @PrePersist en la entidad ya asigna la fecha automáticamente
        dao.guardar(detalle);
    }

    public void actualizar(DetalleVisita detalleVisita) {
        dao.actualizar(detalleVisita);
    }
    
 
    
}