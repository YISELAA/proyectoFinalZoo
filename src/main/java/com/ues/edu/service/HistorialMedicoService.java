/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.service;

import com.ues.edu.daos.HistorialMedicoDao;
import com.ues.edu.entidades.HistorialMedico;
import java.util.List;

/**
 *
 * @author coc44
 */
public class HistorialMedicoService {

    private HistorialMedicoDao dao = new HistorialMedicoDao();

    public void crearHistorial(HistorialMedico h) {
        dao.guardar(h);
    }

    public void editarHistorial(HistorialMedico h) {
        dao.actualizar(h);
    }

   
    public void eliminarHistorial(int id) {
        throw new UnsupportedOperationException("La eliminación de registros médicos está deshabilitada por auditoría clínica.");
        
        /* dao.eliminar(id);
        */
    }

    public List<HistorialMedico> obtenerHistoriales() {
        return dao.listar();
    }

    public HistorialMedico buscarHistorial(int id) {
        return dao.buscarPorId(id);
    }

    public List<HistorialMedico> buscarPorDiagnostico(String diagnostico) {
        return dao.buscarPorDiagnostico(diagnostico);
    }

    public List<HistorialMedico> filtrarPorFecha(java.util.Date fecha) {
        return dao.filtrarPorFecha(fecha);
    }

    public List<HistorialMedico> filtrarPorVeterinario(int idVeterinario) {
        return dao.filtrarPorVeterinario(idVeterinario);
    }

    public List<HistorialMedico> filtrarPorAnimal(int idAnimal) {
        return dao.filtrarPorAnimal(idAnimal);
    }

    public List<HistorialMedico> listarPaginado(int pagina, int size) {
        return dao.listarPaginado(pagina, size);
    }
}