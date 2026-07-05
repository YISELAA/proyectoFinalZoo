package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.ues.edu.service.PermisosService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PermisosServlet", urlPatterns = {"/PermisosServlet"})
public class PermisosServlet extends HttpServlet {

    private final PermisosService service = new PermisosService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idRolStr = request.getParameter("idRol");
        
        if (idRolStr != null && !idRolStr.isEmpty()) {
            int idRol = Integer.parseInt(idRolStr);
            List<Integer> idsAsignados = service.obtenerIdsOpcionesPorRol(idRol);
            
            String json = gson.toJson(idsAsignados);
            response.getWriter().write(json);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Falta el parámetro idRol\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            DtoPermisos datos = gson.fromJson(request.getReader(), DtoPermisos.class);

            if (datos != null) {
                boolean guardado = service.guardarPermisos(datos.idRol, datos.opciones);

                if (guardado) {
                    response.getWriter().write("{\"mensaje\":\"Los permisos se actualizaron correctamente.\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\":\"No se pudieron guardar los cambios en la base de datos.\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Error al procesar la petición: " + e.getMessage() + "\"}");
        }
    }

    // Clase interna (DTO) para mapear el JSON que envía el JS
    private static class DtoPermisos {
        int idRol;
        List<Integer> opciones;
    }
}