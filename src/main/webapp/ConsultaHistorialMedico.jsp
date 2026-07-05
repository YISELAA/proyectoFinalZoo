<%-- 
    Document   : MostrarHistorialMedico
    Created on : 15 may 2026, 10:47:24
    Author     : MINED
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">
        <link rel="stylesheet" href="css/ConsultasCss.css"></head>

    <body>

        <div class="header">
            <h2>🩺 Historial Médico de Animales</h2>
        </div>

        <div class="container">

            <div class="card card-clean shadow-sm">

                <div class="table-responsive">

                    <table id="tablaHistorial" class="table table-hover">
                        <thead>
                            <tr>
                                <th>Animal</th>
                                <th>Especie</th>
                                <th>Diagnóstico</th>
                                <th>Tratamiento</th>
                                <th>Fecha</th>
                                <th>Veterinario</th>
                            </tr>
                        </thead>

                        <tbody></tbody>
                    </table>

                </div>

                <div class="mt-4 text-end">
                    <a href="index.jsp" class="btn-back">
                        ← 
                    </a>
                </div>

            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>

        <script src="js/ConsultaHistorialMedico.js"></script>

    </body>
</html>