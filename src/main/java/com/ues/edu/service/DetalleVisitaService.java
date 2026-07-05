package com.ues.edu.service;

import com.ues.edu.daos.DetalleVisitaDao;
import com.ues.edu.entidades.DetalleVisita;
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
       
        dao.guardar(detalle);
    }

    public void actualizar(DetalleVisita detalleVisita) {
        dao.actualizar(detalleVisita);
    }
    
 
    
}