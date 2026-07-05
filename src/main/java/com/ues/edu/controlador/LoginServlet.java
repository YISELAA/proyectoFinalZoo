package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.ues.edu.entidades.Usuario;
import com.ues.edu.service.LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private final Gson gson = new Gson();
    LoginService service = new LoginService();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("logout".equals(accion)) {
            HttpSession session = request.getSession(false);

            if (session != null) {
                session.removeAttribute("usuarioSesion");
                session.invalidate();
            }

            response.sendRedirect("login.jsp");
            return;
        }

        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            Usuario login = gson.fromJson(request.getReader(), Usuario.class);

            Usuario usuario = service.login(
                    login.getNombreUsuario(),
                    login.getContrasena()
            );

            if (usuario != null) {

                request.getSession().setAttribute("usuarioSesion", usuario);

                String json = String.format(
                        "{\"redirect\":\"index.jsp\",\"nombreUsuario\":\"%s\"}",
                        usuario.getNombreUsuario()
                );

                response.getWriter().write(json);
                return;
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Usuario o contraseña incorrectos.\"}");

        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error interno en el servidor: " + e.getMessage() + "\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}