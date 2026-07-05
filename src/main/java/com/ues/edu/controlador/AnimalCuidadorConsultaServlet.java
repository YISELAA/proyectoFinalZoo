/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.ues.edu.daos.ConsultaAnimalCuidadorDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author MINED
 */
@WebServlet(name = "AnimalCuidadorConsultaServlet", urlPatterns = {"/AnimalCuidadorConsultaServlet"})
public class AnimalCuidadorConsultaServlet extends HttpServlet {

    ConsultaAnimalCuidadorDao dao = new ConsultaAnimalCuidadorDao();

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
            out.println("<title>Servlet AnimalCuidadorConsultaServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AnimalCuidadorConsultaServlet at " + request.getContextPath() + "</h1>");
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
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {

            String filtro = request.getParameter("filtro");

            List<Object[]> datos = dao.buscarFiltro(filtro);

            JSONArray jsonArray = new JSONArray();

            if (datos != null) {
                for (Object[] a : datos) {

                    JSONObject obj = new JSONObject();
                    
                    obj.put("id", a[0] != null ? a[0].toString() : "");
                    obj.put("nombre_animal", a[1] != null ? a[1].toString() : "");
                    obj.put("especie", a[2] != null ? a[2].toString() : "");
                    obj.put("sexo", a[3] != null ? a[3].toString() : "");
                    obj.put("nombre_empleado", a[4] != null ? a[4].toString() : "");
                    obj.put("apellido", a[5] != null ? a[5].toString() : "");
                    obj.put("numero_dui", a[6] != null ? a[6].toString() : "");
                    jsonArray.put(obj);
                }
            }

            out.print(jsonArray);

        } catch (Exception e) {

            e.printStackTrace();

            JSONObject error = new JSONObject();
            error.put("error", e.toString());

            out.print(error);
        }

        out.flush();
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
        processRequest(request, response);
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