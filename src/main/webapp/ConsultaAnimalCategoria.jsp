<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Lista de Animales</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- DataTables Bootstrap 5 -->
        <link rel="stylesheet" href="https://cdn.datatables.net/2.0.8/css/dataTables.bootstrap5.min.css">
        <link rel="stylesheet" href="css/ConsultasCss.css">
    </head>
    <body>
        <div class="header">
            <h1>🐾 LISTA DE ANIMALES</h1>
        </div>
        <div class="container">
            <div class="card card-clean shadow-sm">

                <table id="tablaAnimales" class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Especie</th>
                            <th>Sexo</th>
                            <th>Fecha Ingreso</th>
                            <th>Fecha Nacimiento</th>
                            <th>Edad</th>
                            <th>Tipo Terreno</th>
                            <th>Capacidad</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
            <!-- BOTÓN -->
            <div class="mt-4 text-end">
                <a href="index.jsp" class="btn-back">
                    ←
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
    <script src="https://cdn.datatables.net/2.0.8/js/dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/2.0.8/js/dataTables.bootstrap5.min.js"></script>
    <script src="js/ConsultaAnimalCategoria.js"></script>
</body>
</html>