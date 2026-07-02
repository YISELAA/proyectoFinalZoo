<%-- 
    Document   : HistorialMedico
    Created on : 17 may. 2026, 19:24:36
    Author     : coc44
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>WILD ZOO MK - Gestión de Historial Médico</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

</head>

<body>

```
<div class="contenedor">

    <div class="header">
        <h1>WILD ZOO MK</h1>
        <p>Gestión de Historial Médico</p>
    </div>

    <div class="contenido">

        <form id="formHistorial">

            <input type="hidden" id="idHistorial">

            <div class="formulario">

                <div class="campo">
                    <label for="fecha">Fecha</label>
                    <input type="date"
                           id="fecha"
                           required>
                </div>

                <div class="campo">
                    <label for="diagnostico">Diagnóstico</label>
                    <input type="text"
                           id="diagnostico"
                           placeholder="Ingrese diagnóstico"
                           required>
                </div>

                <div class="campo">
                    <label for="tratamiento">Tratamiento</label>
                    <input type="text"
                           id="tratamiento"
                           placeholder="Ingrese tratamiento"
                           required>
                </div>

                <div class="campo">
                    <label for="idAnimal">Animal</label>
                    <select id="idAnimal" required>
                        <option value="">
                            Seleccione un animal...
                        </option>
                    </select>
                </div>


                <div class="botones">

                    <button type="submit"
                            id="btnGuardar"
                            class="guardar">
                        Guardar Historial
                    </button>

                    <button type="button"
                            class="cancelar"
                            onclick="limpiarFormularioHistorial()">
                        Cancelar
                    </button>

                </div>

            </div>

        </form>

        <div id="mensajeError"></div>

        <table id="tablaHistorial">

            <thead>
                <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Diagnóstico</th>
                    <th>Tratamiento</th>
                    <th>Animal</th>
                    <th>Especie</th>
                    <th>Veterinario</th>
                    <th>Acciones</th>
                </tr>
            </thead>

            <tbody id="tbodyHistoriales">
            </tbody>

        </table>

        <div id="paginacion" class="paginacion"></div>

        <div style="margin-top: 24px; text-align: right;">
            <a href="${pageContext.request.contextPath}/index.jsp"
               class="btn-back">
                <i class="ti ti-arrow-left"></i>
            </a>
        </div>

    </div>

</div>

<script src="${pageContext.request.contextPath}/js/HistorialMedico.js"></script>

</body>

</html>
