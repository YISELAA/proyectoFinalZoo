/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", function () {
    const tbody = document.querySelector("#tablaAlimentacion tbody");
    let table = null;
    function initDataTable() {
        table = $('#tablaAlimentacion').DataTable({
            autoWidth: false,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50],
            pagingType: "simple_numbers", 
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

        fetch("AlimentacionAnimalConsuServlet")
            .then(res => {
                console.log("Estatus de la respuesta del servidor:", res.status); 
                if (!res.ok) throw new Error("Error en la respuesta del servidor");
                return res.json();
            })
            .then(data => {
                console.log("Datos crudos recibidos del Servlet:", data); // 🌟 Alerta 4

                if (table !== null) {
                    table.destroy();
                }

                let html = "";
                data.forEach(item => {
                    html += `
                        <tr>
                            <td>${item.nombre_animal}</td>
                            <td>${item.tipo_alimento}</td>
                            <td>${item.cantidad}</td>
                            <td>${item.horario}</td>
                            <td>${item.cuidador}</td>
                        </tr>
                    `;
                });

                tbody.innerHTML = html;
                initDataTable();
                $('#tablaAlimentacion').DataTable().columns.adjust().draw();
            })
            .catch(error => {
                console.error("Error crítico atrapado en el catch:", error); 
            });
    }

    cargar();
});