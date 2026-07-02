<%-- 
    Document   : ConsultaMostrarEmpleados
    Created on : 1 jul 2026, 20:55:54
    Author     : Mariella
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consulta de Empleados</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    
    <link rel="stylesheet" href="https://cdn.datatables.net/2.0.8/css/dataTables.bootstrap5.min.css">
    
    <link rel="stylesheet" href="css/ConsultasCss.css">
</head>
<body>

<div class="header">
    <h1>👤 PERSONAL DEL ZOOLÓGICO</h1>
    <p>Listado general del personal activo y cuentas asignadas</p>
</div>

<div class="container">

    <div class="card-clean shadow-sm">

        <div class="table-responsive">
            <table id="tablaAnimales" class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th class="text-end">Acciones</th>
                    </tr>
                </thead>
                <tbody id="tablaCuerpoEmpleados">
                    </tbody>
            </table>
        </div>

        <div class="mt-4 text-end">
            <a href="index.jsp" class="btn-back">
                ← Volver al inicio
            </a>
        </div>

    </div>
</div>

<div class="modal fade" id="modalPerfilEmpleado" tabindex="-1" aria-labelledby="modalPerfilLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content" style="border-radius: 20px; border: none; background-color: var(--blanco-puro); box-shadow: 0 10px 30px rgba(8, 28, 21, 0.15);">
      
      <div class="modal-header text-white" style="border-top-left-radius: 20px; border-top-right-radius: 20px; background-color: var(--color-consultas); border-bottom: none; padding: 20px 24px;">
        <h5 class="modal-title fw-bold" id="modalPerfilLabel">📋 Perfil Detallado del Empleado</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      
      <div class="modal-body p-4 text-center" style="background-color: var(--blanco-puro); border-bottom-left-radius: 20px; border-bottom-right-radius: 20px;">
        <div id="modalContenidoPerfil"></div>
      </div>
      
    </div>
  </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/2.0.8/js/dataTables.min.js"></script>
<script src="https://cdn.datatables.net/2.0.8/js/dataTables.bootstrap5.min.js"></script>

<script src="js/ConsultaEmpleados.js"></script>

</body>
</html>