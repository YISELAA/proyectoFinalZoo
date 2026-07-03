/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", function () {

    const tbody = document.querySelector("#tablaHistorial tbody");

    let table = null;

    function initDataTable() {

        table = $('#tablaHistorial').DataTable({

            pageLength: 5,

            lengthMenu: [5, 10, 25, 50],

            language: {

                lengthMenu: "Mostrar _MENU_ registros",

                info: "Mostrando _START_ a _END_ de _TOTAL_ registros",

                paginate: {
                    next: "Siguiente",
                    previous: "Anterior"
                }
            }
        });
    }

    function cargar() {

        fetch("/ProyectoFinalZoo/HistorialMedicoConsuServlet")

                .then(res => res.json())

                .then(data => {

                    let html = "";

                    data.forEach(item => {

                        html += `
                        <tr>
                            <td>${item.nombre_animal}</td>
                            <td>${item.especie}</td>
                            <td>${item.diagnostico}</td>
                            <td>${item.tratamiento}</td>
                            <td>${item.fecha}</td>
                            <td>${item.veterinario} ${item.apellido}</td>
                        </tr>
                    `;
                    });

                    tbody.innerHTML = html;

                    // Inicializar DataTable
                    if (!$.fn.DataTable.isDataTable("#tablaHistorial")) {

                        initDataTable();

                    } else {

                        table.destroy();
                        initDataTable();
                    }
                })

                .catch(error => {

                    console.log(error);
                });
    }

    cargar();
});