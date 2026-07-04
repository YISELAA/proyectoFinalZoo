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
import java.io.BufferedReader;

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

        // Leer el JSON recibido
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String linea;

        while ((linea = reader.readLine()) != null) {
            sb.append(linea);
        }

        String jsonRaw = sb.toString();

        System.out.println("========== JSON RECIBIDO ==========");
        System.out.println(jsonRaw);

        // Convertir JSON a objeto
        Usuario usuario = gson.fromJson(jsonRaw, Usuario.class);

        System.out.println("========== OBJETO DESERIALIZADO ==========");
        System.out.println(gson.toJson(usuario));

        // Validaciones
        String error = validarUsuario(usuario, false);

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + error + "\"}");
            return;
        }

        // Guardar
        usuarioService.crearUsuario(usuario);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"mensaje\":\"Usuario guardado exitosamente\"}");

    } catch (RuntimeException e) {

        System.out.println("========== ERROR DE NEGOCIO ==========");
        e.printStackTrace();   // <-- MUY IMPORTANTE

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");

    } catch (Exception e) {

        System.out.println("========== ERROR INTERNO ==========");
        e.printStackTrace();

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"error\":\"Error interno del servidor\"}");
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

        Usuario usuario = gson.fromJson(request.getReader(), Usuario.class);

        System.out.println("========== USUARIO RECIBIDO EN PUT ==========");
        System.out.println(gson.toJson(usuario));

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

        System.out.println("========== ERROR DE NEGOCIO EN PUT ==========");
        e.printStackTrace();   // <-- IMPORTANTE: imprime la causa completa

        // Si existe una causa interna (Hibernate/SQL), también la imprime
        if (e.getCause() != null) {
            System.out.println("========== CAUSA ==========");
            e.getCause().printStackTrace();
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");

    } catch (Exception e) {

        System.out.println("========== ERROR CRÍTICO EN PUT ==========");
        e.printStackTrace();

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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