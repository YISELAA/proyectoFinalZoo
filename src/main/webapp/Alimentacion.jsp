<%-- 
    Document   : Alimentacion
    Created on : 18 may. 2026, 22:02:32
    Author     : coc44
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>WILD ZOO MK - Gestión de Usuarios</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>

    <div class="contenedor">

        <div class="header">
            <h1>WILD ZOO MK</h1>
            <p>Gestión de Alimentación</p>
        </div>

        <div class="contenido">

            <form id="formAlimentacion">

                <input type="hidden" id="idAlimentacion">

                <div class="formulario">

                    <div class="campo">
                        <label for="tipoAlimento">Tipo Alimento</label>
                        <input type="text"
                               id="tipoAlimento"
                               placeholder="Ingrese alimento"
                               required>
                    </div>

                    <div class="campo">
                        <label for="horario">Horario</label>
                        <input type="text"
                               id="horario"
                               placeholder="Ej: 08:00 AM"
                               required>
                    </div>

                    <div class="campo"> <label>Cantidad</label> <div style="display: flex; width: 100%;"> <input type="number" step="0.01" id="cantidad" placeholder="Ingrese cantidad" required style="border-radius: 12px 0 0 12px; border-right: none; flex: 1;"> <span style="display: flex; align-items: center; padding: 0 15px; background: var(--verde-suave); border: 2px solid #cce3de; border-left: none; border-radius: 0 12px 12px 0; color: var(--verde-oscuro); font-weight: bold; font-size: 15px;"> kg </span> </div> </div>

                    <div class="campo">
                        <label for="idAnimal">Especie</label>

                        <select id="idAnimal" required>
                            <option value="">
                                Seleccione una especie
                            </option>
                        </select>
                    </div>

                    <div class="botones">

                        <button type="submit"
                                id="btnGuardar"
                                class="guardar">
                            Guardar Alimentación
                        </button>

                        <button type="button"
                                class="cancelar"
                                onclick="limpiarFormulario()">
                            Cancelar
                        </button>

                    </div>

                </div>

            </form>

            <div id="mensajeError"></div>

            <table id="tablaAlimentacion">

                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tipo Alimento</th>
                        <th>Horario</th>
                        <th>Cantidad (kg)</th>
                        <th>Animal</th>
                        <th>Cuidador</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody id="tbodyAlimentacion">
                </tbody>

            </table>

            <div id="paginacion" class="paginacion"></div>

            <div style="margin-top: 24px; text-align: right;">
                <a href="${pageContext.request.contextPath}/index.jsp" class="btn-back">
                    <i class="ti ti-arrow-left"></i>
                </a>
            </div>

        </div>

    </div>

    <script src="${pageContext.request.contextPath}/js/Alimentacion.js"></script>

</body>

</html>