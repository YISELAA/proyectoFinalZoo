<%@page import="com.ues.edu.daos.JPAUtil"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>

<%@ page import="jakarta.persistence.EntityManager" %>

<%@ page import="org.hibernate.Session" %>

<%@ page import="net.sf.jasperreports.engine.JasperRunManager" %>

<%
EntityManager em = null;

try {

    em = JPAUtil.getEMF().createEntityManager();

    Session sessionHibernate = em.unwrap(Session.class);

    final Connection[] conn = new Connection[1];

    sessionHibernate.doWork(connection -> {
        conn[0] = connection;
    });

    File reportfile = new File(
        application.getRealPath("/Reportes/ReporteVentasEmpleados.jasper")
    );

    if (!reportfile.exists()) {
        throw new FileNotFoundException(
            "No existe el archivo: " + reportfile.getAbsolutePath()
        );
    }

    // Parámetros del reporte
    Map<String, Object> parameters = new HashMap<>();

    parameters.put("Admin", "Admin");

    // Generar PDF
    byte[] bytes = JasperRunManager.runReportToPdf(
        reportfile.getAbsolutePath(),
        parameters,
        conn[0]
    );

    // Enviar PDF al navegador
    response.reset();
    response.setContentType("application/pdf");
    response.setContentLength(bytes.length);

    ServletOutputStream outStream = response.getOutputStream();
    outStream.write(bytes);
    outStream.flush();
    outStream.close();

} catch (Exception e) {

    response.setContentType("text/html;charset=UTF-8");

    out.println("<h2>Error al generar el reporte</h2>");
    out.println("<pre>");
    e.printStackTrace(new java.io.PrintWriter(out));
    out.println("</pre>");

} finally {

    if (em != null && em.isOpen()) {
        em.close();
    }

}
%>