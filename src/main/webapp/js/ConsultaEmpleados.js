/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", function () {
    console.log("¡JS de Consulta de Empleados detectado y cargado correctamente!"); 

    const tbody = document.querySelector("#tablaCuerpoEmpleados");
    let table = null;

    function cargar() {
        console.log("Iniciando petición fetch al Servlet de Empleados..."); 
        
        // El path relativo de tu servlet mapeado
        fetch("EmpleadosConsultaServlet")
                .then(res => {
                    if (!res.ok) throw new Error("Error en la respuesta del servidor");
                    return res.json();
                })
                .then(data => {
                    console.log("Datos crudos de empleados recibidos:", data);

                    // Si ya existía una tabla activa de DataTables, la destruimos antes de reescribir
                    if ($.fn.DataTable.isDataTable('#tablaAnimales')) {
                        $('#tablaAnimales').DataTable().destroy();
                    }

                    let html = "";

                    if (data.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-4">No hay empleados registrados.</td></tr>';
                        return;
                    }

                    data.forEach(item => {
                        // Control de nulos seguro para pasar al modal
                        let usuario = item.nombre_usuario ? `@${item.nombre_usuario}` : "Sin usuario asignado";
                        let rol = item.nombre_rol ? item.nombre_rol : "Sin Rol asignado";
                        let nombre = item.nombre_empleado ? item.nombre_empleado : "";
                        let apellido = item.apellido ? item.apellido : "";
                        let dui = item.numero_dui ? item.numero_dui : "N/A";
                        let id = item.id_empleado ? item.id_empleado : "S/N";

                        html += `
                            <tr>
                                <td class="py-3 px-4 fw-medium" style="color: var(--texto-oscuro); font-size: 15px;">${nombre}</td>
                                <td class="py-3 px-4" style="color: #2b3a34; font-size: 15px;">${apellido}</td>
                                <td class="py-3 px-4 text-end">
                                    <button class="btn btn-sm rounded-pill px-3 fw-bold text-white shadow-sm" 
                                            style="background-color: var(--color-reportes); border: none; font-size: 13px; transition: all 0.2s;"
                                            onmouseover="this.style.backgroundColor='var(--color-crud)'"
                                            onmouseout="this.style.backgroundColor='var(--color-reportes)'"
                                            onclick="verPerfilCompleto('${nombre} ${apellido}', '${rol}', '${usuario}', '${dui}', '${id}')">
                                        <i class="bi bi-eye-fill me-1"></i> Ver Perfil
                                    </button>
                                </td>
                            </tr>
                        `;
                    });

                    tbody.innerHTML = html;
                    
                    // Inicializar y ajustar tal cual tu ejemplo
                    initDataTable();
                    $('#tablaAnimales').DataTable().columns.adjust().draw();
                })
                .catch(error => {
                    console.error("Error al cargar datos de empleados:", error);
                });
    }

    cargar();
});

function initDataTable() {
    table = $('#tablaAnimales').DataTable({
        autoWidth: false,
        pageLength: 5,
        lengthMenu: [5, 10, 25, 50],
        pagingType: "simple_numbers",
        language: {
            lengthMenu: "Mostrar _MENU_ registros",
            info: "Mostrando _START_ a _END_ de _TOTAL_ registros",
            search: "Buscar",
            infoEmpty: "No hay registros",
            zeroRecords: "No se encontraron resultados",
            paginate: {
                next: "→",
                previous: "←"
            }
        }
    });
}

/**
 * Función global encargada de estructurar el modal detallado e invocarlo
 */
function verPerfilCompleto(nombreCompleto, rol, usuario, dui, id) {
    let contenido = `
        <div class="text-center mb-4">
            <div class="avatar-circle mx-auto mb-2" style="width: 70px; height: 70px; background-color: var(--color-consultas); color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; border-radius: 50%; font-size: 24px; box-shadow: 0 4px 8px rgba(8,28,21,0.15);">
                ${nombreCompleto.charAt(0).toUpperCase()}
            </div>
            <h4 class="fw-bold text-dark mb-1">${nombreCompleto}</h4>
            <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill text-uppercase px-3 py-1" style="font-size: 11px; font-weight: 600;">
                ${rol}
            </span>
        </div>
        
        <div class="text-start bg-light p-3 rounded-3 border border-light-subtle" style="font-size: 14px;">
            <div class="d-flex justify-content-between align-items-center mb-2 pb-2 border-bottom border-secondary-subtle">
                <span class="text-muted">ID Registro:</span>
                <span class="fw-semibold text-secondary">#${id}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center mb-2 pb-2 border-bottom border-secondary-subtle">
                <span class="text-muted">Nombre de Usuario:</span>
                <span class="fw-semibold text-dark bg-white border px-2 rounded-2">${usuario}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center">
                <span class="text-muted">Documento DUI:</span>
                <span class="text-dark font-monospace fw-semibold">${dui}</span>
            </div>
        </div>
    `;
    
    document.getElementById("modalContenidoPerfil").innerHTML = contenido;
    var myModal = new bootstrap.Modal(document.getElementById('modalPerfilEmpleado'));
    myModal.show();
}