package com.ues.edu.controlador;

import com.ues.edu.daos.ConsultaHistorialMedicoDao;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "HistorialMedicoConsuServlet", urlPatterns = {"/HistorialMedicoConsuServlet"})
public class HistorialMedicoConsuServlet extends HttpServlet {

    ConsultaHistorialMedicoDao dao = new ConsultaHistorialMedicoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String filtro = request.getParameter("filtro");

            List<Object[]> historial = dao.buscarFiltro(filtro);

            JSONArray jsonArray = new JSONArray();

            // 🔥 FORMATO DE FECHA UNIFICADO
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            if (historial != null) {

                for (Object[] h : historial) {

                    JSONObject obj = new JSONObject();

                    obj.put("nombre_animal", h[0] != null ? h[0].toString() : "");
                    obj.put("diagnostico", h[1] != null ? h[1].toString() : "");
                    obj.put("tratamiento", h[2] != null ? h[2].toString() : "");

                    // 🔥 AQUÍ ESTÁ LA CORRECCIÓN IMPORTANTE
                    obj.put("fecha",
                            h[3] != null ? sdf.format(h[3]) : ""
                    );

                    obj.put("veterinario", h[4] != null ? h[4].toString() : "");
                    obj.put("apellido", h[5] != null ? h[5].toString() : "");

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Historial Médico Consulta Servlet";
    }
}