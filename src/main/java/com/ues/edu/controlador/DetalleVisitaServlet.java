package com.ues.edu.controlador;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.ues.edu.entidades.DetalleVisita;
import com.ues.edu.entidades.Ticket;
import com.ues.edu.entidades.Usuario;
import com.ues.edu.service.DetalleVisitaService;
import com.ues.edu.service.TicketService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "DetalleVisitaServlet", urlPatterns = {"/DetalleVisitaServlet"})
public class DetalleVisitaServlet extends HttpServlet {

    DetalleVisitaService service = new DetalleVisitaService();
    TicketService ticketService = new TicketService();

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.toString()))
            .addSerializationExclusionStrategy(
                    new ExclusionStrategy() {

                @Override
                public boolean shouldSkipField(FieldAttributes f) {

                    return f.getName().equals("habitatsAsignados")
                            || f.getName().equals("cuidadores")
                            || f.getName().equals("historiales")
                            || f.getName().equals("usuario")
                            || f.getName().equals("listaAnimales");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {

            int id = Integer.parseInt(idParam);
            DetalleVisita detalleVisita = service.buscarDetalleVisita(id);

            if (detalleVisita == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Registro no encontrado\"}");
                return;
            }

            response.getWriter().write(gson.toJson(detalleVisita));
            return;
        }

        List<DetalleVisita> lista = service.listarDetalleVisita();

        System.out.println("ANTES DE JSON");

        String json = gson.toJson(lista);

        System.out.println("DESPUES DE JSON");

        response.getWriter().write(json);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");

        DetalleVisita detalleVisita = gson.fromJson(request.getReader(), DetalleVisita.class);

        // Buscar el ticket real por tipo para tener precio e id
        if (detalleVisita.getTicket() != null
                && detalleVisita.getTicket().getTipo() != null) {

            Ticket ticketReal = ticketService.buscarPorTipo(
                    detalleVisita.getTicket().getTipo()
            );

            if (ticketReal == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Ticket no encontrado\"}");
                return;
            }

            detalleVisita.setTicket(ticketReal);

        
            detalleVisita.setSubtotal(
                    detalleVisita.getCantidad() * ticketReal.getPrecio()
            );
        }

        String error = validarDetalleVisita(detalleVisita);

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + error + "\"}");
            return;
        }
        // Usuario que inició sesión
      
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        Usuario usuarioLogueado = null;

        if (session != null) {
            
            usuarioLogueado = (Usuario) session.getAttribute("usuarioSesion");
        }

        // Escudo de seguridad: Si no hay usuario en sesión, respondemos con error JSON en lugar de un 500 HTML
        if (usuarioLogueado == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Error 401
            response.getWriter().write("{\"error\":\"Sesión inválida o expirada. Por favor, vuelva a iniciar sesión.\"}");
            return;
        }

        // El empleado se asigna directo desde el objeto recuperado
        detalleVisita.setEmpleado(usuarioLogueado.getEmpleado());
        
        service.guardarDetalleVisita(detalleVisita);
        response.getWriter().write("{\"mensaje\":\"Visita guardada\"}");
    }

    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");

        try {
            DetalleVisita detalleVisita = gson.fromJson(request.getReader(), DetalleVisita.class);

            if (detalleVisita.getTicket() != null
                    && detalleVisita.getTicket().getTipo() != null) {

                Ticket ticketReal = ticketService.buscarPorTipo(
                        detalleVisita.getTicket().getTipo()
                );

                if (ticketReal == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"Ticket no encontrado\"}");
                    return;
                }

                detalleVisita.setTicket(ticketReal);
                detalleVisita.setSubtotal(
                        detalleVisita.getCantidad() * ticketReal.getPrecio()
                );
            }

            String error = validarDetalleVisita(detalleVisita);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }

            service.actualizar(detalleVisita);
            response.getWriter().write("{\"mensaje\":\"Visita actualizada\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

   
    private String validarDetalleVisita(DetalleVisita d) {

        if (d == null) {
            return "Datos inválidos";
        }

        if (d.getNombreVisitante() == null
                || d.getNombreVisitante().trim().length() < 3) {
            return "Nombre mínimo 3 caracteres";
        }

        if (!d.getNombreVisitante().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            return "El nombre del visitante solo debe contener letras, sin números";
        }

        if (d.getTelefono() == null
                || !d.getTelefono().matches("\\d{8}")) {
            return "Teléfono debe tener 8 dígitos";
        }

        if (d.getCantidad() == null || d.getCantidad() <= 0) {
            return "Cantidad debe ser mayor que 0";
        }

        if (d.getTicket() == null) {
            return "Debe seleccionar un ticket";
        }

        return null; 
    }
}
