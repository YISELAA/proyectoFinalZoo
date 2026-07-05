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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HistorialMedicoServlet", urlPatterns = {"/HistorialMedicoServlet"})
public class HistorialMedicoServlet extends HttpServlet {

    private final HistorialMedicoService historialService = new HistorialMedicoService();

    private final Gson gson = new GsonBuilder()
            .addSerializationExclusionStrategy(new com.google.gson.ExclusionStrategy() {

                @Override
                public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
                    String name = f.getName();

                    return name.equals("historiales")
                            || name.equals("cuidadores")
                            || name.equals("animalesAsignados")
                            || name.equals("usuario")
                            || name.equals("habitatsAsignados")
                            || name.equals("listaAnimales");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .setDateFormat("yyyy-MM-dd")
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");

        HttpSession session = request.getSession(false);

        Usuario user = (session != null)
                ? (Usuario) session.getAttribute("usuarioSesion")
                : null;

       
        if ("obtenerSesion".equals(accion)) {

            if (user == null || user.getEmpleado() == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"No hay sesión activa\"}");
                return;
            }

            // SOLO ENVIAMOS EL EMPLEADO (veterinario)
            response.getWriter().write(gson.toJson(user.getEmpleado()));
            return;
        }

       
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

       
        List<HistorialMedico> lista = historialService.obtenerHistoriales();
        response.getWriter().write(gson.toJson(lista));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session = request.getSession(false);

            Usuario user = (session != null)
                    ? (Usuario) session.getAttribute("usuarioSesion")
                    : null;

            if (user == null || user.getEmpleado() == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Sin sesión\"}");
                return;
            }

            HistorialMedico historial
                    = this.gson.fromJson(request.getReader(), HistorialMedico.class);

            System.out.println("===== HISTORIAL =====");
            System.out.println("Fecha: " + historial.getFecha());
            System.out.println("Diagnóstico: " + historial.getDiagnostico());
            System.out.println("Tratamiento: " + historial.getTratamiento());
            System.out.println("Animal: " + (historial.getAnimal() != null ? historial.getAnimal().getId() : "NULL"));
            System.out.println("Veterinario sesión: " + user.getEmpleado().getId());

            historial.setVeterinario(user.getEmpleado());

            String error = validarHistorial(historial);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }
            System.out.println("===== DEBUG =====");
            System.out.println("Usuario: " + user.getNombreUsuario());

            if (user.getEmpleado() != null) {
                System.out.println("Empleado ID: " + user.getEmpleado().getId());
            } else {
                System.out.println("Empleado: NULL");
            }

            if (historial.getAnimal() != null) {
                System.out.println("Animal ID: " + historial.getAnimal().getId());
            } else {
                System.out.println("Animal: NULL");
            }

            System.out.println("Entrando a guardar...");
            historialService.crearHistorial(historial);
            System.out.println("Guardó correctamente");

            response.getWriter().write("{\"mensaje\":\"Historial guardado exitosamente\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error servidor\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");

        try {

            HistorialMedico h = gson.fromJson(request.getReader(), HistorialMedico.class);

            historialService.editarHistorial(h);

            response.getWriter().write("{\"mensaje\":\"Historial actualizado exitosamente\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"No se permite eliminar historiales médicos\"}");
    }

  
    private String validarHistorial(HistorialMedico h) {

        if (h == null) {
            return "Historial inválido";
        }

        String regexTexto = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s.,\\-]+$";

        if (h.getDiagnostico() == null || h.getDiagnostico().trim().length() < 5) {
            return "Diagnóstico mínimo 5 caracteres";
        }
        if (!h.getDiagnostico().matches(regexTexto)) {
            return "El diagnóstico solo debe contener letras";
        }

        if (h.getTratamiento() == null || h.getTratamiento().trim().length() < 5) {
            return "Tratamiento mínimo 5 caracteres";
        }
        if (!h.getTratamiento().matches(regexTexto)) {
            return "El tratamiento solo debe contener letras";
        }

        if (h.getFecha() == null) {
            return "Fecha requerida";
        }

        java.time.LocalDate fechaHistorial = h.getFecha()
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        if (fechaHistorial.isAfter(java.time.LocalDate.now())) {
            return "¡La fecha del historial clínico no puede ser una fecha futura!";
        }

        if (h.getAnimal() == null) {
            return "Debe asociar un animal";
        }

        return null;
    }
}
