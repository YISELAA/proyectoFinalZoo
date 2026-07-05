/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.ues.edu.daos.ConsultaAlimentacionAnimalDao;
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
@WebServlet(name = "AlimentacionAnimalConsuServlet", urlPatterns = {"/AlimentacionAnimalConsuServlet"})
public class AlimentacionAnimalConsuServlet extends HttpServlet {

    ConsultaAlimentacionAnimalDao dao = new ConsultaAlimentacionAnimalDao();
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
            out.println("<title>Servlet AlimentacionAnimalConsuServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AlimentacionAnimalConsuServlet at " + request.getContextPath() + "</h1>");
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

            List<Object[]> alimentaciones = dao.buscarFiltro();

            JSONArray jsonArray = new JSONArray();

            if (alimentaciones != null) {

                for (Object[] a : alimentaciones) {

                    JSONObject obj = new JSONObject();

                    obj.put("nombre_animal", a[0] != null ? a[0].toString() : "");
                    obj.put("tipo_alimento", a[1] != null ? a[1].toString() : "");
                    obj.put("cantidad", a[2] != null ? a[2].toString() : "");
                    obj.put("horario", a[3] != null ? a[3].toString() : "");
                    obj.put("cuidador", a[4] != null ? a[4].toString() : "—"); 

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
