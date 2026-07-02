/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", function () {
    console.log("¡JS de Consulta de Historial Médico detectado correctamente!");

    const tbody = document.querySelector("#tablaHistorial tbody");
    let table = null;

    function initDataTable() {
        table = $('#tablaHistorial').DataTable({
            autoWidth: false,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50],
            pagingType: "simple_numbers", // Activa la paginación corta con flechas
            destroy: true,
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

    function cargar() {
        console.log("Iniciando petición fetch al Servlet de Historial...");
        
        fetch("/ProyectoFinalZoo/HistorialMedicoConsuServlet")
            .then(res => res.json())
            .then(data => {
                console.log(data);

                // Destruimos la tabla antes de renderizar si ya existía una instancia activa
                if ($.fn.DataTable.isDataTable("#tablaHistorial")) {
                    $('#tablaHistorial').DataTable().destroy();
                }

                let html = "";
                
                // Usando 'a' como en tu ejemplo de ConsultaCuidadorAnimal
                data.forEach(a => {
                    html += `
                        <tr>
                            <td>${a.nombre_animal}</td>
                            <td>${a.diagnostico}</td>
                            <td>${a.tratamiento}</td>
                            <td>${a.fecha}</td>
                            <td>${a.veterinario} ${a.apellido}</td>
                        </tr>
                    `;
                });

                tbody.innerHTML = html;

                // Inicializamos DataTables y ajustamos el redibujado de columnas
                initDataTable();
                $('#tablaHistorial').DataTable().columns.adjust().draw();
            })
            .catch(error => {
                console.error("Error al cargar datos:", error);
            });
    }

    cargar();
});