/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.service;

import com.ues.edu.daos.TicketDao;
import com.ues.edu.entidades.Ticket;
import java.util.List;

/**
 *
 * @author MINED
 */
public class TicketService {

    private TicketDao dao = new TicketDao();

    public List<Ticket> obtenerTickets() {
        return dao.listarTodos();
    }

    public void registrarTicket(Ticket p) {

        String tipoNormalizado = p.getTipo().trim().toLowerCase();

        Ticket existente = dao.buscarPorTipo(tipoNormalizado);

        if (existente != null) {
            throw new RuntimeException("Ya existe un ticket registrado con ese nombre");
        }

        p.setTipo(tipoNormalizado); 
        dao.guardar(p);
    }

    public void actualizarTicket(Ticket p) {
        dao.actualizar(p);
    }

    public void deshabilitar(Integer id) {
        dao.deshabilitar(id);
    }

    public void habilitar(Integer id) {
        dao.habilitar(id);
    }

    public Ticket obtenerTicketPorId(int id) {
        return dao.buscarPorId(id);
    }

    public Ticket buscarPorTipo(String tipo) {
        return dao.buscarPorTipo(tipo);
    }
}
