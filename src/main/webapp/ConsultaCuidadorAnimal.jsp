<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Animales y Cuidadores</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.13.8/css/jquery.dataTables.min.css">

        <link rel="stylesheet" href="css/ConsultasCss.css">
    </head>

    <body>

        <div class="header">
            <h1>🐾 LISTA DE ANIMALES Y CUIDADORES</h1>
        </div>

        <div class="container">

            <div class="card card-clean shadow-sm">

                <div class="table-responsive">
                    <table id="tablaAC" class="table table-hover">
                        <thead> 
                        <th>ID</th>
                        <th>Nombre Animal</th>
                        <th>Especie</th>
                        <th>Nombre de Cuidador</th>
                        <th>Apellido</th>
                        <th>DUI</th>
                        </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>

                <div class="mt-3 text-end">
                    <a href="index.jsp" class="btn-back">← </a>
                </div>

            </div>

        </div>

        <link rel="stylesheet" href="https://cdn.datatables.net/2.0.8/css/dataTables.bootstrap5.min.css">

        <!-- jQuery -->
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
        <!-- DataTables core + Bootstrap 5 -->
        <script src="https://cdn.datatables.net/2.0.8/js/dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/2.0.8/js/dataTables.bootstrap5.min.js"></script>

        <script src="js/ConsultaCuidadorAnimal.js"></script>

    </body>
</html>