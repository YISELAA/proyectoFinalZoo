/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.service.UsuariosService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.ues.edu.entidades.Usuario;

/**
 *
 * @author MINED
 */
@WebServlet(name = "UssuarioServlet", urlPatterns = {"/UssuarioServlet"})
public class UssuarioServlet extends HttpServlet {

    private final UsuariosService usuarioService = new UsuariosService();

    // 🌟 Instancia global de Gson configurada con estrategia de exclusión segura
    private final Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setExclusionStrategies(new com.google.gson.ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return clazz.getName().contains("hibernate")
                    || clazz.getName().contains("HibernateProxy");
            }
        })
        .create();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UssuarioServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UssuarioServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String idParam = request.getParameter("id");

            // BUSCAR POR ID (Cuando das clic en Editar)
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Usuario usuario = usuarioService.buscarUsuario(id);

                if (usuario == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"mensaje\":\"Usuario no encontrado\"}");
                    return;
                }

                response.getWriter().write(this.gson.toJson(usuario));
                return;
            }

            // LISTAR TODOS (Cuando carga la tabla)
            List<Usuario> usuarios = usuarioService.mostrarUsuarios();
            response.getWriter().write(this.gson.toJson(usuarios));

        } catch (Exception e) {
            System.out.println("ERROR EN DO_GET: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al obtener datos: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try {
            // 1. Leer el cuerpo de la petición como texto plano
            java.io.BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
            String jsonRaw = sb.toString();
            
            System.out.println("JSON recibido en bruto: " + jsonRaw);

            // 2. Deserializar el JSON entrante
            Usuario usuario = this.gson.fromJson(jsonRaw, Usuario.class);

            // 3. Validar los datos sintácticos esenciales
            String error = validarUsuario(usuario, false);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }

            // 4. Mandar a guardar usando el servicio
            usuarioService.crearUsuario(usuario);

            // 5. Responder con éxito genuino
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"mensaje\":\"Usuario guardado exitosamente\"}");

        } catch (RuntimeException e) {
            // 🚨 CAPTURA AQUÍ: Errores lógicos preventivos del negocio (Ej. "El nombre de usuario ya existe")
            System.out.println("VALIDACIÓN DE NEGOCIO EN DO_POST: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");

        } catch (Exception e) {
            // Error técnico físico imprevisto de base de datos o servidor
            System.out.println("ERROR CRÍTICO INESPERADO EN DO_POST: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"error\":\"Error interno en el servidor: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Handles the HTTP <code>PUT</code> method.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Usuario usuario = this.gson.fromJson(request.getReader(), Usuario.class);

            String error = validarUsuario(usuario, true);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }

            usuarioService.actualizarUsuario(usuario);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"mensaje\":\"Usuario actualizado con éxito\"}");

        } catch (RuntimeException e) {
            // 🚨 CAPTURA AQUÍ: Errores controlados del Service en actualizaciones si los implementas
            System.out.println("VALIDACIÓN DE NEGOCIO EN DO_PUT: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");

        } catch (Exception e) {
            System.out.println("ERROR CRÍTICO INESPERADO EN DO_PUT: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"error\":\"Error al actualizar: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Falta el ID del usuario\"}");
                return;
            }

            int id = Integer.parseInt(idParam);
            System.out.println("DELETE ID = " + id);
            
            usuarioService.eliminarUsuario(id);
            System.out.println("SERVICIO ELIMINAR EJECUTADO");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"mensaje\":\"Usuario deshabilitado exitosamente\"}");

        } catch (Exception e) {
            System.out.println("ERROR EN DO_DELETE: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al eliminar: " + e.getMessage() + "\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private String validarUsuario(Usuario u, boolean esActualizacion) {
        if (u == null) {
            return "Usuario inválido";
        }

        if (u.getNombreUsuario() == null || u.getNombreUsuario().trim().length() < 7) {
            return "El nombre de usuario debe tener mínimo 7 caracteres";
        }

        if (!esActualizacion) {
            if (u.getContrasena() == null || u.getContrasena().trim().length() < 6) {
                return "La contraseña debe tener mínimo 6 caracteres";
            }
        }
        return null;
    }
}