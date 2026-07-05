package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.entidades.Ticket;
import com.ues.edu.service.TicketService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TicketServlet", urlPatterns = {"/TicketServlet"})
public class TicketServlet extends HttpServlet {

    private TicketService ticketService = new TicketService();

    private Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {

            Integer id = Integer.valueOf(idParam);

            Ticket ticket = ticketService.obtenerTicketPorId(id);

            if (ticket == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"mensaje\":\"Ticket no encontrado\"}");
                return;
            }

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(ticket));
            return;
        }

        List<Ticket> tickets = ticketService.obtenerTickets();

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(tickets));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            Ticket ticket = gson.fromJson(request.getReader(), Ticket.class);

            String error = validarTicket(ticket);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }

            ticketService.registrarTicket(ticket);

            response.getWriter().write("{\"mensaje\":\"Ticket guardado\"}");

        } catch (RuntimeException e) {
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error interno del servidor\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String accion = request.getHeader("X-Accion");

        System.out.println("ACCION RECIBIDA: " + accion);

        if ("habilitar".equals(accion)) {
            Integer id = Integer.valueOf(request.getParameter("id"));
            ticketService.habilitar(id);
            response.setContentType("application/json");
            response.getWriter().write("{\"mensaje\":\"Ticket habilitado\"}");
            return;
        }

        Ticket ticket = gson.fromJson(request.getReader(), Ticket.class);
        String error = validarTicket(ticket);
        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + error + "\"}");
            return;
        }
        ticketService.actualizarTicket(ticket);
        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Ticket actualizado\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Integer id = Integer.valueOf(request.getParameter("id"));
        ticketService.deshabilitar(id);
        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Ticket deshabilitado\"}");
    }

    private String validarTicket(Ticket t) {

        if (t == null) {
            return "Ticket inválido";
        }

        if (t.getTipo() == null || t.getTipo().trim().length() < 4) {
            return "El tipo debe tener mínimo 4 caracteres";
        }
        
         if (!t.getTipo().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            return "El tipo de ticket no debe contener números ni caracteres especiales";
        }

        if (t.getPrecio() <= 0) {
            return "El precio debe ser mayor a 0";
        }

        return null;
    }
}
