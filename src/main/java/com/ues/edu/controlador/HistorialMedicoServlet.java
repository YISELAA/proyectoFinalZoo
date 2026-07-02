/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.entidades.HistorialMedico;
import com.ues.edu.entidades.Usuario;
import com.ues.edu.service.HistorialMedicoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author coc44
 */
@WebServlet(name = "HistorialMedicoServlet", urlPatterns = {"/HistorialMedicoServlet"})
public class HistorialMedicoServlet extends HttpServlet {

    private HistorialMedicoService historialService = new HistorialMedicoService();

   private Gson gson = new GsonBuilder()
        .addSerializationExclusionStrategy(new com.google.gson.ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
                // Bloqueamos todas las listas y relaciones profundas que el frontend no necesita
                return f.getName().equals("historiales")
                    || f.getName().equals("cuidadores")
                    || f.getName().equals("animalesAsignados")
                    || f.getName().equals("usuario")
                        || f.getName().equals("habitatsAsignados")
                    || f.getName().equals("listaAnimales"); // <--- EL NUEVO CULPABLE AQUÍ
            }
            @Override
            public boolean shouldSkipClass(Class<?> clazz) { return false; }
        })
        .setDateFormat("dd-MM-yyyy") 
        .create();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HistorialMedicoServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HistorialMedicoServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // 🌟 1. CAPTURAR SI EL FRONTEND PIDE LA SESIÓN
    String accion = request.getParameter("accion");
    if ("obtenerSesion".equals(accion)) {
        // Obtenemos la sesión actual (sin crear una nueva si no existe)
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        
        if (session != null && session.getAttribute("usuario") != null) {
            Usuario userLogueado = (Usuario) session.getAttribute("usuario");
            
            // Si el usuario tiene un empleado asociado, lo enviamos al JS
            if (userLogueado.getEmpleado() != null) {
                response.getWriter().write(gson.toJson(userLogueado.getEmpleado()));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"El usuario no tiene un empleado/veterinario asignado\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"No hay una sesión activa\"}");
        }
        return; // Detiene la ejecución aquí para que no intente listar todo lo demás
    }

    // --- FLUJO NORMAL DE TU SERVLET QUE YA TENÍAS ---
    String idParam = request.getParameter("id");

    if (idParam != null && !idParam.isEmpty()) {
        int id = Integer.parseInt(idParam);
        HistorialMedico h = historialService.buscarHistorial(id);

        if (h == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"mensaje\":\"Historial no encontrado\"}");
            return;
        }
        response.getWriter().write(gson.toJson(h));
        return;
    }

    List<HistorialMedico> historiales = historialService.obtenerHistoriales();
    response.getWriter().write(gson.toJson(historiales));
}
        
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // USAMOS EL GSON GLOBAL QUE TIENE LAS EXCLUSIONES Y EL FORMATO CORRECTO
            HistorialMedico historial = this.gson.fromJson(request.getReader(), HistorialMedico.class);
            
            String error = validarHistorial(historial);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }

            // Guardamos en la base de datos
            historialService.crearHistorial(historial);

            // Enviamos mensaje de éxito
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"mensaje\":\"Historial guardado exitosamente\"}");
            
        } catch (Exception e) {
            e.printStackTrace(); 
           
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");

            String mensajeError = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error desconocido";
            response.getWriter().write("{\"error\":\"Error en el servidor: " + mensajeError + "\"}");
        }
    }
    
    // ===============================
    // PUT → actualizar historial
    // ===============================
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // ¡CORREGIDO! Usamos el gson global configurado con "yyyy-MM-dd" y ExclusionStrategy
            HistorialMedico historial = this.gson.fromJson(request.getReader(), HistorialMedico.class);

            historialService.editarHistorial(historial); 

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"mensaje\":\"Historial actualizado exitosamente\"}");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            String mensajeError = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error al actualizar";
            response.getWriter().write("{\"error\":\"Error en el servidor al editar: " + mensajeError + "\"}");
        }
    }
    
    @Override
protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    
    // 🚫 Bloqueado por reglas de negocio clínicas
    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"error\":\"Por normativa de auditoría, los historiales médicos no pueden ser eliminados del sistema.\"}");
}

    // ===============================
    // VALIDACIÓN
    // ===============================
    private String validarHistorial(HistorialMedico h) {
        if (h == null) {
            return "Historial inválido";
        }
        if (h.getDiagnostico() == null || h.getDiagnostico().trim().length() < 5) {
            return "Diagnóstico mínimo 5 caracteres";
        }
        if (h.getTratamiento() == null || h.getTratamiento().trim().length() < 5) {
            return "Tratamiento mínimo 5 caracteres";
        }
        if (h.getFecha() == null) {
            return "Fecha requerida";
        }
        if (h.getAnimal() == null) {
            return "Debe asociar un animal";
        }
        if (h.getVeterinario() == null) {
            return "Debe asociar un veterinario";
        }
        return null;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
