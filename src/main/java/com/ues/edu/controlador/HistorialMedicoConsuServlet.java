/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.ues.edu.daos.ConsultaHistorialMedicoDao;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author coc44
 */
@WebServlet(name = "HistorialMedicoConsuServlet", urlPatterns = {"/HistorialMedicoConsuServlet"})
public class HistorialMedicoConsuServlet extends HttpServlet {
    
    ConsultaHistorialMedicoDao dao = new ConsultaHistorialMedicoDao();

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
            out.println("<title>Servlet HistorialMedicoConsuServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HistorialMedicoConsuServlet at " + request.getContextPath() + "</h1>");
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

            List<Object[]> historial = (List<Object[]>) dao.buscarFiltro(filtro);

            JSONArray jsonArray = new JSONArray();

            if (historial != null) {

                for (Object[] h : historial) {

                    JSONObject obj = new JSONObject();

                    obj.put("nombre_animal", h[0] != null ? h[0].toString() : "");
                    obj.put("especie", h[1] != null ? h[1].toString() : "");
                    obj.put("diagnostico", h[2] != null ? h[2].toString() : "");
                    obj.put("tratamiento", h[3] != null ? h[3].toString() : "");
                    obj.put("fecha", h[4] != null ? h[4].toString() : "");
                    obj.put("veterinario", h[5] != null ? h[5].toString() : "");
                    obj.put("apellido", h[6] != null ? h[6].toString() : "");

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
        
            doGet(request, response);
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